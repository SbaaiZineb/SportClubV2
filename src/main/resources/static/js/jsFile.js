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

}
function calculateEndDate() {
    var startDate = new Date(document.getElementById("startDateId").value);
    var selectedOption = document.getElementById("abonnementId");
    var selectedPeriod = parseInt(selectedOption.options[selectedOption.selectedIndex].getAttribute("data-period"));

    if (!isNaN(selectedPeriod) && startDate) {
        if (selectedPeriod === 0) {
            // Set end date to null if selected period is "0"
            document.getElementById("endDateId").value = "";
        } else {
            var endDate = new Date(startDate);
            endDate.setMonth(endDate.getMonth() + selectedPeriod);

            var endDateInput = document.getElementById("endDateId");
            var formattedEndDate = endDate.toISOString().split('T')[0]; // Format as "YYYY-MM-DD"
            endDateInput.value = formattedEndDate;
        }
    }
}
function capturePic() {
let camera_button = document.querySelector("#start-camera");
let video = document.querySelector("#video");
let click_button = document.querySelector("#click-photo");
let canvas = document.querySelector("#canvas");



camera_button.addEventListener('click', async function() {
    let stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: false });
    video.srcObject = stream;

    // Show video, click button, canvas, and image name input
    video.style.display = "block";
    click_button.style.display = "block";
    canvas.style.display = "block";
});
let currentDate = new Date().toISOString().slice(0, 10).replace(/-/g, '');
let currentTime = new Date().toLocaleTimeString().replace(/:/g, '');
let randomName = `user_${currentDate}_${currentTime}`;

click_button.addEventListener('click', function() {
    canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);
    let image_data_url = canvas.toDataURL('image/jpeg');

    let downloadLink = document.createElement('a');
    downloadLink.href = image_data_url;
    downloadLink.download = `${randomName}.jpeg`;
    downloadLink.click();
});}
function display(){
    // Make the "Nomber des séance" field appear only when selecting "Carnet"
    const periodDropdown = document.getElementById('periodDropdown');
    const nbrSeanceLabel = document.getElementById('nbrSeanceLabel');
    const nbrSeanceDropdown = document.getElementById('nbrSeance');
    const perDiv = document.getElementById('perDiv');
    const startDateInput = document.getElementById('startDateId');
    const endDateInput = document.getElementById('endDateId');

    // Event listener to the period dropdown
    periodDropdown.addEventListener('change', function () {
        // Check if the selected value is "Carnet"
        if (this.value === '0') {
            // If selected, display the nbrSeanceLabel and nbrSeanceDropdown
            nbrSeanceLabel.style.display = 'block';
            nbrSeanceDropdown.style.display = 'block';
        }else if(this.value === 'per'){
            nbrSeanceLabel.style.display = 'none';
            nbrSeanceDropdown.style.display = 'none';
            perDiv.style.display = 'block';
            // Add required attribute to startDate and endDate inputs
            startDateInput.required = true;
            endDateInput.required = true;
        } else {
            // If another option is selected, hide the nbrSeanceLabel and nbrSeanceDropdown
            nbrSeanceLabel.style.display = 'none';
            nbrSeanceDropdown.style.display = 'none';
            // Remove required attribute from startDate and endDate inputs
            startDateInput.required = false;
            endDateInput.required = false;
        }
    });}