document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('register-form');
    const submitBtn = document.getElementById('submit-btn');
    const statusMessage = document.getElementById('status-message');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        // UI Loading state
        const originalBtnText = submitBtn.innerText;
        submitBtn.innerText = "Registering...";
        submitBtn.disabled = true;
        statusMessage.className = 'hidden';

        // Gather form data
        const formData = new URLSearchParams();
        formData.append('name', document.getElementById('name').value);
        formData.append('phoneNumber', document.getElementById('phoneNumber').value);
        formData.append('email', document.getElementById('email').value);
        formData.append('password', document.getElementById('password').value);

        try {
            const response = await fetch('/api/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData.toString()
            });

            if (response.ok) {
                statusMessage.textContent = "Account created successfully!";
                statusMessage.className = "success";
                form.reset();
            } else {
                const errorText = await response.text();
                statusMessage.textContent = errorText || "Registration failed.";
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
