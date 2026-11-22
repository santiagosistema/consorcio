document.addEventListener("DOMContentLoaded", function () {

    // Obtém o token CSRF da página principal.
    function obterHeadersCSRF() {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        const headers = {
            "Content-Type": "application/json"
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        return headers;
    }

    // Envia uma requisição POST com CSRF.
    async function enviarPost(url) {
        return fetch(url, {
            method: "POST",
            headers: obterHeadersCSRF(),
            credentials: "same-origin"
        });
    }

    // =====================================================
    // MARCAR UMA NOTIFICAÇÃO COMO LIDA
    // Funciona mesmo com o fragmento carregado dinamicamente.
    // =====================================================

    document.addEventListener("click", async function (event) {

        const link = event.target.closest(
            ".notification-item[data-notificacao-id]"
        );

        if (!link) {
            return;
        }

        const id = link.dataset.notificacaoId;

        if (!id) {
            return;
        }

        event.preventDefault();

        const destino = link.getAttribute("href") || "/home";

        // Evita duplo clique enquanto a requisição é processada.
        if (link.dataset.processando === "true") {
            return;
        }

        link.dataset.processando = "true";

        try {
            const resposta = await enviarPost(
                "/api/notificacoes/" + encodeURIComponent(id) + "/ler"
            );

            if (!resposta.ok) {
                console.error(
                    "Erro ao marcar notificação como lida:",
                    resposta.status,
                    await resposta.text()
                );

                alert(
                    "Não foi possível marcar a notificação como lida. " +
                    "Erro HTTP: " + resposta.status
                );

                return;
            }

            // Atualiza o visual da notificação.
            link.classList.remove("notification-unread");

            link.querySelectorAll(".notification-unread-dot")
                .forEach(elemento => elemento.remove());

            // Atualiza o contador do sino.
            atualizarContador();

            // Abre a página de destino.
            window.location.href = destino;

        } catch (erro) {
            console.error("Erro ao processar notificação:", erro);

            alert("Ocorreu um erro ao processar a notificação.");

        } finally {
            link.dataset.processando = "false";
        }
    });


    // =====================================================
    // MARCAR TODAS AS NOTIFICAÇÕES COMO LIDAS
    // =====================================================

    document.addEventListener("click", async function (event) {

        const botao = event.target.closest("#btn-marcar-todas-lidas");

        if (!botao) {
            return;
        }

        event.preventDefault();

        if (botao.dataset.processando === "true") {
            return;
        }

        botao.dataset.processando = "true";
        botao.disabled = true;

        try {
            const resposta = await enviarPost(
                "/api/notificacoes/marcar-todas-lidas"
            );

            if (!resposta.ok) {
                console.error(
                    "Erro ao marcar todas como lidas:",
                    resposta.status,
                    await resposta.text()
                );

                alert(
                    "Não foi possível marcar todas as notificações como lidas. " +
                    "Erro HTTP: " + resposta.status
                );

                return;
            }

            // Remove os indicadores visuais.
            document.querySelectorAll(".notification-unread")
                .forEach(elemento => {
                    elemento.classList.remove("notification-unread");
                });

            document.querySelectorAll(".notification-unread-dot")
                .forEach(elemento => elemento.remove());

            atualizarContador();

        } catch (erro) {
            console.error("Erro ao atualizar notificações:", erro);

            alert("Ocorreu um erro ao atualizar as notificações.");

        } finally {
            botao.dataset.processando = "false";
            botao.disabled = false;
        }
    });


    // =====================================================
    // ATUALIZA O CONTADOR DO SINO
    // =====================================================

    function atualizarContador() {
        const notificacoesNaoLidas = document.querySelectorAll(
            ".notification-item.notification-unread"
        ).length;

        const botaoSino = document.getElementById("btn-notifications");

        if (!botaoSino) {
            return;
        }

        let badge = botaoSino.querySelector(".navbar-action-badge");

        if (notificacoesNaoLidas === 0) {
            if (badge) {
                badge.remove();
            }

            return;
        }

        if (!badge) {
            badge = document.createElement("span");
            badge.className = "navbar-action-badge";
            botaoSino.appendChild(badge);
        }

        badge.textContent = notificacoesNaoLidas;
    }

});