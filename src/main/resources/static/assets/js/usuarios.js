// Busca os usuários ativos para distribuir as cotas
async function carregarUsuariosAtivos(urlGrupo) {

    const urlUsuarios = urlGrupo.replace("/grupo", "/usuario") + "/ativos";

    const resposta = await fetch(urlUsuarios);

    if (!resposta.ok) {
        throw new Error("Erro ao buscar usuários ativos.");
    }

    return await resposta.json();
}


function criarCamposDistribuicao(usuarios) {

    const distribuicao = document.getElementById("distribuicaoCotas");
    const lista = document.getElementById("listaDistribuicaoCotas");

    lista.replaceChildren();

    if (usuarios.length === 0) {
        distribuicao.classList.add("d-none");
        return;
    }

    usuarios.forEach(function (usuario, indice) {

        const linha = document.createElement("div");
        linha.className = "row g-2 mb-3";

        const colunaUsuario = document.createElement("div");
        colunaUsuario.className = "col-8";

        const select = document.createElement("select");
        select.className = "form-select";
        select.name = "usuarios";
        select.required = true;

        const opcaoInicial = document.createElement("option");
        opcaoInicial.value = "";
        opcaoInicial.textContent = "Selecione o usuário";
        select.appendChild(opcaoInicial);

        usuarios.forEach(function (u) {
            const opcao = document.createElement("option");
            opcao.value = u.id;
            opcao.textContent = u.nome;
            select.appendChild(opcao);
        });

        // Sugere um usuário diferente em cada linha.
        select.value = String(usuario.id);

        const colunaQuantidade = document.createElement("div");
        colunaQuantidade.className = "col-4";

        const quantidade = document.createElement("input");
        quantidade.type = "number";
        quantidade.className = "form-control";
        quantidade.name = "quantidades";
        quantidade.min = "1";
        quantidade.step = "1";
        quantidade.required = true;
        quantidade.placeholder = "Cotas";
        quantidade.setAttribute("aria-label", `Quantidade de cotas do usuário ${indice + 1}`);

        colunaUsuario.appendChild(select);
        colunaQuantidade.appendChild(quantidade);

        linha.appendChild(colunaUsuario);
        linha.appendChild(colunaQuantidade);

        lista.appendChild(linha);
    });

    distribuicao.classList.remove("d-none");
}





document.addEventListener("DOMContentLoaded", () => {

    const lista = document.getElementById("listaUsuarios");
    const busca = document.getElementById("busca");
    const btnAnterior = document.getElementById("btnAnterior");
    const btnProxima = document.getElementById("btnProxima");
    const infoPagina = document.getElementById("infoPagina");
    const mensagem = document.getElementById("msg");

    const url = lista.dataset.url;

    let paginaAtual = 0;
    const tamanhoPagina = 10;

    async function carregarUsuarios(pagina = 0) {

        mensagem.textContent = "";
        lista.innerHTML = "<p>Carregando usuários...</p>";

        try {
            const response = await fetch(
                `${url}?page=${pagina}&size=${tamanhoPagina}`
            );

            if (!response.ok) {
                throw new Error("Erro ao consultar usuários.");
            }

            const resultado = await response.json();

			/* resultado além dos dados coletados, tem os dados da paginação */
			
			/* A primeira linha sincroniza a variável paginaAtual com o índice efetivamente devolvido pelo backend. */
			/* A segunda envia somente o array de usuários para a função responsável por montar os cards. */
			/* A terceira envia o objeto completo para a função responsável pelos controles da paginação. */
            paginaAtual = resultado.number;
            renderizarUsuarios(resultado.content);
            atualizarPaginacao(resultado);

        } catch (error) {
            lista.innerHTML = "";
            mensagem.textContent = "Não foi possível carregar os usuários.";
            mensagem.className = "alert alert-danger";
            console.error(error);
        }
    }

	
	/* A função renderizarUsuarios(): criando os cards */
    function renderizarUsuarios(usuarios) {

        lista.innerHTML = "";

        if (usuarios.length === 0) {
            lista.innerHTML =
                "<p class='text-center'>Nenhum usuário encontrado.</p>";
            return;
        }

        usuarios.forEach(usuario => {

            const card = document.createElement("div");
            card.className = "card mb-3 shadow-sm";

            const corpo = document.createElement("div");
            corpo.className = "card-body";

            const nome = document.createElement("h5");
            nome.className = "card-title";
            nome.textContent = usuario.nome ?? "";
			nome.style.color = "#006400";
			nome.style.marginBottom = "10px";
			nome.style.borderBottom = "1px solid #dee2e6";
			nome.style.paddingBottom = "8px";
			nome.style.width = "100%";

            const cpf = document.createElement("p");
            cpf.className = "card-text";
            cpf.textContent = `CPF: ${usuario.cpf ?? ""}`;

            const email = document.createElement("p");
            email.className = "card-text";
            email.textContent = `E-mail: ${usuario.email ?? ""}`;

            const situacao = document.createElement("p");
            situacao.className = "card-text";
            situacao.textContent = usuario.situacao
                ? "Situação: Ativo"
                : "Situação: Inativo";
				
			const administrador = document.createElement("p");
			administrador.className = "card-text";
			administrador.textContent = usuario.administrador
			    ? "Tipo: Administrador"
			    : "Tipo: Usuário";

            const botaoEditar = document.createElement("button");
            botaoEditar.type = "button";
            botaoEditar.className = "btn-custom btn-custom-secondary";
            botaoEditar.textContent = "Editar";

            botaoEditar.addEventListener("click", () => {
                editarUsuario(usuario.id);
            });

            corpo.append(nome, cpf, email, situacao, administrador, botaoEditar);
            card.appendChild(corpo);
            lista.appendChild(card);
        });
    }

    function atualizarPaginacao(resultado) {

        const totalPaginas = resultado.totalPages;
        const totalRegistros = resultado.totalElements;

        infoPagina.textContent = totalPaginas === 0
            ? "Nenhum registro"
            : `Página ${paginaAtual + 1} de ${totalPaginas} · ${totalRegistros} usuários`;

        btnAnterior.disabled = resultado.first;
        btnProxima.disabled = resultado.last;
    }

    btnAnterior.addEventListener("click", () => {
        if (paginaAtual > 0) {
            carregarUsuarios(paginaAtual - 1);
        }
    });

    btnProxima.addEventListener("click", () => {
        carregarUsuarios(paginaAtual + 1);
    });

    // Pesquisa nos usuários da página atualmente carregada.
    // A busca em todos os registros será implementada no backend.
    busca.addEventListener("input", () => {

        const termo = busca.value.trim().toLowerCase();

        lista.querySelectorAll(".card").forEach(card => {
            card.hidden = !card.textContent.toLowerCase().includes(termo);
        });
    });

    carregarUsuarios();
});