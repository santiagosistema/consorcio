
(function () {
    const metadadoToken = document.querySelector('meta[name="_csrf"]');
    const metadadoCabecalho = document.querySelector('meta[name="_csrf_header"]');

    if (!metadadoToken || !metadadoCabecalho) {
        console.warn("Metadados CSRF não encontrados nesta página.");
        return;
    }

    const token = metadadoToken.content;
    const nomeCabecalho = metadadoCabecalho.content;

    // Intercepta as chamadas fetch da aplicação.
    const fetchOriginal = window.fetch.bind(window);

    window.fetch = function (recurso, opcoes = {}) {
        const metodo = (opcoes.method || "GET").toUpperCase();

        if (["POST", "PUT", "PATCH", "DELETE"].includes(metodo)) {
            const cabecalhos = new Headers(opcoes.headers || {});

            if (!cabecalhos.has(nomeCabecalho)) {
                cabecalhos.set(nomeCabecalho, token);
            }

            opcoes = {
                ...opcoes,
                headers: cabecalhos
            };
        }

        return fetchOriginal(recurso, opcoes);
    };
})();
