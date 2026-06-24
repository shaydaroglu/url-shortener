const form = document.getElementById("shorten-form");
const input = document.getElementById("original-url");
const resultBox = document.getElementById("result");
const errorBox = document.getElementById("error");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    clearMessages();

    const originalUrl = input.value.trim();

    const button = form.querySelector("button");
    button.disabled = true;
    button.textContent = "Creating...";

    try {
        const response = await fetch("/api/v1/urls", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                original_url: originalUrl
            })
        });

        const data = await readJsonSafely(response);

        if (!response.ok) {
            showError(data);
            return;
        }

        showResult(data);
        input.value = "";
    } catch (error) {
        showError({
            title: "Unexpected error",
            status: 0,
            detail: "Could not reach the server. Please try again."
        });
    } finally {
        button.disabled = false;
        button.textContent = "Shorten URL";
    }
});

async function readJsonSafely(response) {
    try {
        return await response.json();
    } catch {
        return {
            title: "Unexpected response",
            status: response.status,
            detail: "Server returned an unreadable response."
        };
    }
}

function showResult(data) {
    resultBox.innerHTML = `
        <strong>Short URL created:</strong>
        <a class="short-link" href="${escapeHtml(data.shortened_url)}" target="_blank" rel="noopener noreferrer">
            ${escapeHtml(data.shortened_url)}
        </a>
        <p>Short code: <strong>${escapeHtml(data.short_code)}</strong></p>
    `;

    resultBox.classList.remove("hidden");
}

function showError(error) {
    errorBox.innerHTML = `
        <strong>${escapeHtml(error.title || "Error")}</strong>
        <p>${escapeHtml(error.detail || "Something went wrong.")}</p>
        ${error.status ? `<small>Status: ${escapeHtml(String(error.status))}</small>` : ""}
    `;

    errorBox.classList.remove("hidden");
}

function clearMessages() {
    resultBox.classList.add("hidden");
    errorBox.classList.add("hidden");
    resultBox.innerHTML = "";
    errorBox.innerHTML = "";
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}