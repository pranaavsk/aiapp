document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('login-form');
    const submitBtn = document.getElementById('submit-btn');
    const statusMessage = document.getElementById('status-message');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // UI Loading state
        const originalBtnText = submitBtn.innerText;
        submitBtn.innerText = "Signing in...";
        submitBtn.disabled = true;
        statusMessage.className = 'hidden';

        // Gather form data
        const formData = new URLSearchParams();
        formData.append('email', document.getElementById('email').value);
        formData.append('password', document.getElementById('password').value);

        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData.toString()
            });

            if (response.status === 200) {
                statusMessage.textContent = "Login successful!";
                statusMessage.className = "success";
                form.reset();
            } else if (response.status === 400) {
                statusMessage.textContent = "Please fill in all required fields.";
                statusMessage.className = "error";
            } else if (response.status === 401) {
                statusMessage.textContent = "Invalid email or password.";
                statusMessage.className = "error";
            } else {
                statusMessage.textContent = "Login failed. Please try again.";
                statusMessage.className = "error";
            }
        } catch (error) {
            statusMessage.textContent = "Network error. Please try again.";
            statusMessage.className = "error";
        } finally {
            submitBtn.innerText = originalBtnText;
            submitBtn.disabled = false;
        }
    });
});
