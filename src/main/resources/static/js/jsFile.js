// Function to handle input validation
function handleInputValidation(inputId, existsMessageId, urlParameter, formId, submitButtonId) {
    var inputElement = document.getElementById(inputId);
    var existsMessageElement = document.getElementById(existsMessageId);

    function updateValidation() {
        var inputValue = inputElement.value;
        var xhr = new XMLHttpRequest();
        xhr.open('GET', urlParameter + inputValue, true);

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    var response = JSON.parse(xhr.responseText);

                    if (response.exists === true) {
                        existsMessageElement.textContent = 'Ce ' + inputId + ' existe déjà.';
                        inputElement.classList.add('email-exists');
                        document.getElementById(submitButtonId).disabled = true;
                        inputElement.classList.add('input-error');
                    } else {
                        existsMessageElement.textContent = '';
                        inputElement.classList.remove('email-exists');
                        document.getElementById(submitButtonId).disabled = false;
                        inputElement.classList.remove('input-error');
                    }
                } else {
                    console.log('Error');
                }
                checkFormValidity(formId, submitButtonId); // Check form validity after each input update
            }
        };

        xhr.send();
    }

    inputElement.addEventListener('blur', updateValidation);
    inputElement.addEventListener('input', updateValidation);
}

// Function to check form validity
function checkFormValidity(formId, submitButtonId) {
    var form = document.getElementById(formId);
    var formInputs = form.querySelectorAll('.required-field');
    var submitButton = document.getElementById(submitButtonId);

    var isFormValid = true;

    formInputs.forEach(function(input) {
        if (!input.checkValidity()) {
            isFormValid = false;
            input.classList.add('input-error');
        } else {
            input.classList.remove('input-error');
        }
    });

    submitButton.disabled = !isFormValid;
}
