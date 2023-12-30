function openForm(type, preFilledData = null) {
    // Implement logic to display the form for adding/editing tasks or habits
    // You can use the 'type' parameter to determine if it's for tasks or habits
    // The 'preFilledData' parameter will contain the data for pre-filling the form

    const formContainer = document.getElementById('form-container');
    
    // Create form content with pre-filled data
    const formContent = `
        <form id="taskHabitForm">
            <label for="title">Title:</label>
            <input type="text" id="title" name="title" value="${preFilledData ? preFilledData.title : ''}" required>
			<br>
            <label for="notes">Notes:</label>
            <textarea id="notes" name="notes" rows=4>${preFilledData ? preFilledData.notes : ''}</textarea>
			<br><!--
			<label for="checkList">Check List:</label>
			<div>
				<input type="text" id="add-checklist" name="add-checklist">
				<span id="addchkBtn" onclick="addChkItem()">+</span>
			</div>
			<ul id="checkList" name="checkList">
                ${preFilledData ? preFilledData.checkList.map(item => 
                	`<div id="${item}">
                		<input type="checkbox" ${item.validated} ? "checked" : ""/>
                		<span>${item}</span>
                		<span>❌</span>
                	</div>`
                ).join('') : ''}
            </ul>
			<br>--><!--
			<div>
            	<label for="reminder">Reminder:</label>
            	<input type="checkbox" id="reminder-check" name="reminder-check" ${(preFilledData && preFilledData.reminder != null) ? "checked" : ""}/>
            </div>
            <input type="datetime-local" id="reminder" name="reminder" value="${preFilledData ? new Date(preFilledData.reminder).toISOString().substring(0,19) : TODAY.toISOString().substring(0,19)}">
	        <br>-->
            ${type === 'task' ? `
				<div>
	                <label for="dueDate">Due Date:</label>
	                <input type="checkbox" id="dueDate-check" name="dueDate-check" ${(preFilledData && preFilledData.dueDate != null) ? "checked" : ""} />
                </div>
                <input type="date" id="dueDate" name="dueDate" min="${TODAY.toISOString().substring(0,10)}" value="${preFilledData ? new Date(preFilledData.dueDate).toISOString().substring(0,10) : TODAY.toISOString().substring(0,10)}">
            ` : ''}

            ${type === 'habit' ? `
                <label for="startDate">Start date:</label>
                <input type="date" id="startDate" name="startDate" value="${preFilledData ? new Date(preFilledData.startDate).toISOString().substring(0,10) : TODAY.toISOString().substring(0,10)}" required/>
		        <br>
		        <label for="frequencyType">Frequency:</label>
				<div>
					<div>
						<input type="radio" id="dayFrequency-radio" name="frequencyType" onclick="selectDivFrequency('dayFrequency')"
							value="DAILY" ${preFilledData && preFilledData.frequencyType == "DAILY" ? "checked" : ""}/>
	    				<label for="dayFrequency-radio">Every X days</label>
	  				</div>
					<div>
						<input type="radio" id="dayOfMonth-radio" name="frequencyType" onclick="selectDivFrequency('dayOfMonth')"
							value="MONTHLY" ${preFilledData && preFilledData.frequencyType == "MONTHLY" ? "checked" : ""}/>
	    				<label for="dayOfMonth-radio">Every month the 3rd</label>
	  				</div>
	  				<div>
						<input type="radio" id="dayOfWeek-radio" name="frequencyType" onclick="selectDivFrequency('dayOfWeek')"
							value="DAYOFWEEK" ${preFilledData && preFilledData.frequencyType == "DAYOFWEEK" ? "checked" : ""}/>
	    				<label for="dayOfWeek-radio">Every monday and wednesday</label>
	  				</div>
	  				<div id="frequencyData">
	  					<div class="frequencyDiv" id="dayFrequency" ${preFilledData && preFilledData.frequencyType == "DAILY" ? "" : 'style="display:none;"'}>
	  						A positive int input for days freq and a time input for time of reset
	  					</div>
	  					<div class="frequencyDiv" id="dayOfMonth" ${preFilledData && preFilledData.frequencyType == "MONTHLY" ? "" : 'style="display:none;"'}>
	  						An input to choose which day of the month
	  					</div>
	  					<div class="frequencyDiv" id="dayOfWeek" ${preFilledData && preFilledData.frequencyType == "DAYOFWEEK" ? "" : 'style="display:none;"'}>
	  						An input to choose which days of the week
	  					</div>
	  				</div>
				</div>
                <br><!--
                <label for="streak">Streak:</label>
	            <input type="number" id="streak" name="streak" value="${preFilledData ? preFilledData.streak : 0}">-->
            ` : ''}
			<br>
            <button type="button" id="saveFormButton" onclick="saveFormItem('${type}', ${preFilledData ? preFilledData.id : 'null'}, ${preFilledData ? preFilledData.sortOrder : 'null'})">
                ${preFilledData ? 'Update' : 'Create'}
            </button>
            <button type="button" id="cancelFormButton" onclick="closeForm();">Cancel</button>
        </form>
    `;

    formContainer.innerHTML = formContent;
    formContainer.style.display = 'block';
    formContainer.classList.add('slideIn');
    const form = document.getElementById('taskHabitForm');
    form.onsubmit = function(e){
	    e.preventDefault();};
}

function selectDivFrequency(div) {
	for (let el of document.getElementsByClassName("frequencyDiv")) {
		if (el.id == div)
			el.style.display = "block";
		else
			el.style.display = "none";
	}
}

function closeForm() {
    // Implement logic to close the form
    const formContainer = document.getElementById('form-container');
    formContainer.classList.add('slideOut');
}

function saveFormItem(type, itemId, sortOrder) {
    // Implement logic to save the item (create or update) based on the form data
    // You can use the 'type' and 'itemId' parameters to determine the action
    // You'll need to gather the form data and send it to the API endpoint
    var form = document.getElementById('taskHabitForm');
    if (!form.checkValidity()) {
    	return;
    }

    const formData = new FormData(form);
    const formDataObject = {};
    formData.forEach((value, key) => {
        formDataObject[key] = value;
    });
    if (itemId) {
    	formDataObject['id'] = itemId;
    	formDataObject['sortOrder'] = sortOrder
    } else {
    	formDataObject['sortOrder'] = document.getElementById(`${type}-list`).childElementCount
    }
    if ('reminder-check' in formDataObject)
    	delete formDataObject['reminder-check']
   	else
   		delete formDataObject['reminder']
   	if ('dueDate-check' in formDataObject)
    	delete formDataObject['dueDate-check']
   	else
   		delete formDataObject['dueDate']
   	saveItem(type, itemId, JSON.stringify(formDataObject));
}

function editItem(type, itemId) {
    // Fetch the data of the item to be edited
    fetch(API + 'get?id=' + itemId)
        .then(response => response.json())
        .then(data => {
            // Open the form for editing with the pre-filled data
            openForm(type, data);
        })
        .catch(error => console.error(`Error fetching ${type} data for editing: ${error}`));
}

{
	const formContainer = document.getElementById('form-container');
	formContainer.addEventListener("animationend", function() {
		if (formContainer.classList.contains('slideOut')) {
			formContainer.classList.remove('slideOut');
			formContainer.style.display = 'none';
		}
		if (formContainer.classList.contains('slideIn')) {
			formContainer.classList.remove('slideIn');
			document.getElementById("title").focus();
		}
	});
}

window.addEventListener('keyup', function(e) {
    if (e.key=='Enter') {
        if (e.target.nodeName=='INPUT' && e.target.type !== 'textarea') {
            e.preventDefault();
            el = document.getElementById("form-container")
            if (el && el.contains(e.target)){
				saveButton = document.getElementById("saveFormButton")
				saveButton.click();
				return true;
            }
            return false;
        }
    } else if (e.key=='Escape') {
    	if (e.target) {
            el = document.getElementById("form-container")
            if (el && el.contains(e.target)){
            	e.preventDefault();
				cancelButton = document.getElementById("cancelFormButton")
				cancelButton.click();
				return true;
            }
            return false;
        }
    }
}, true);