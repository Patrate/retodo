API = 'http://127.0.0.1:8080/api/'
TODAY = new Date();
TODAY.setHours(0, 0, 3, 0)
var config = [];

// INITIAL LOAD

document.addEventListener('DOMContentLoaded', function () {
    // Fetch and display tasks and habits
    loadConfig();
    fetchData('task', 'task-list');
    fetchData('habit', 'habit-list');
});

// CONFIG

function loadConfig() {
   fetch(`${API}config`)
    .then(response => response.json())
    .then(data => {
    	data.forEach(el => config[el.key] = el.value);
    	setColor(`#${config["color"]}`);
    })
    .catch(error => console.error(`Error fetching config: ${error}`));
}

function setConfig(key, val) {
	fetch(`${API}config?key=${key}&value=${val}`, {
        method: 'PUT',
    })
        .then(response => {
            if (response.ok) {
                loadConfig();
            } else {
                console.error(`Error seting ${key} to ${val}. Status: ${response.status}`);
            }
        })
        .catch(error => console.error(`Error setting ${key} to ${value}: ${error}`));
}

function setColor(color) {
	var bck = color;
	var border = newShade(color, -50);
	var box = newShade(color, -30);
	document.body.style.backgroundColor = color;
	document.getElementById("colorPicker").value = color;
	els = document.getElementsByClassName("container");
	for(let el of els) {
		el.style.backgroundColor = box;
		el.style.borderColor = border;
	}
}

// QUICKADD

const qatask = document.getElementsByName("quickadd-task")[0];
qatask.addEventListener("keyup", ({key}) => {
    if (key === "Enter") {
        if(qatask.value != null && qatask.value != "") {
        	json = `{"title":"${qatask.value}",
        		"sortOrder":${document.getElementById("task-list").childElementCount}}`;
        	saveItem("task", null, json);
        }
        qatask.value = ""
    }
})

const qahabit = document.getElementsByName("quickadd-habit")[0];
qahabit.addEventListener("keyup", ({key}) => {
    if (key === "Enter") {
        if(qahabit.value != null && qahabit.value != "") {
        	json = `{"title":"${qahabit.value}",
        		"sortOrder":${document.getElementById("habit-list").childElementCount}}`;
        	saveItem("habit", null, json);
        }
        qahabit.value = ""
    }
})

// DATA HANDLERS

function fetchData(endpoint, listId) {
    // Fetch data from the API
    fetch(`${API}${endpoint}s`)
        .then(response => response.json())
        .then(data => {
        	//sort
        	data.sort(sortFct)
        	reindex(endpoint, data)
        	// Populate the list with fetched data
            const list = document.getElementById(listId);
            list.innerHTML = ''; // Clear existing items

            data.forEach(item => {
            	if(endpoint == 'task' && item.validated) {
            		return;
            	}
                const listItem = document.createElement('li');
                listItem.setAttribute('id', item.id);
                listItem.setAttribute('draggable', true);
                listItem.innerHTML = `
                	<label class="checkbox-control">
                    	<input type="checkbox" ${item.validated ? 'checked' : ''} onchange='check(this, ${item.id},"${endpoint}");'/>
                    </label>
                    <div class="taskname" onclick="editItem('${endpoint}', ${item.id})">
                    	<div>${(item.title == null || item.title == "") ? "[undefined]" : item.title}</div>
                    </div>
                    <div>
                    	<span onclick="deleteItem(this.parentElement.parentElement, '${endpoint}', ${item.id})">❌</span>
                    </div>
                `;
                if (item.validated) {
                	listItem.classList.add("checked");
                } else {
	                listItem.classList.add("unchecked");
                }
	            list.appendChild(listItem);
            });
        })
        .catch(error => console.error(`Error fetching ${endpoint} data: ${error}`));
}

function saveItem(type, itemId, json, noreload) {
    fetch(API + (itemId ? ('edit?id=' + itemId) : ('new?type=' + type.toUpperCase())), {
        method: itemId ? 'PUT' : 'POST', // Use PUT for updating, POST for creating
        headers: {
            'Content-Type': 'application/json',
        },
        body: json,
    })
        .then(response => response.json())
        .then(data => {
            closeForm();
            if(noreload != true)
            	fetchData(`${type}`, `${type}-list`);
        })
        .catch(error => console.error(`Error saving ${type} item: ${error}`));
}

function check(chkbx, itemId, type) {
    if(type == 'task')
	    chkbx.parentElement.parentElement.classList.add('fadeOut')
	else {
		chkbx.parentElement.parentElement.classList.remove(chkbx.checked ? "unchecked" : "checked");
		chkbx.parentElement.parentElement.classList.add(chkbx.checked ? "checked" : "unchecked");
	}	
	fetch(`${API}check?id=${itemId}&valid=${chkbx.checked}`, {
        method: 'PUT'
    })
}

function deleteItem(li, type, itemId) {
    // Confirm deletion with the user
    const confirmDelete = confirm('Are you sure you want to delete this item?');
    if (!confirmDelete) {
        return; // Do nothing if the user cancels the deletion
    }

    // Delete the item using the API
    fetch(`${API}delete?id=${itemId}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (response.ok) {
                // Handle successful deletion
                console.log(`${type} item deleted successfully.`);
                // Reload the updated list
                li.classList.add('fadeOut')
                li.addEventListener("animationend", function() {
					fetchData(`${type}`, `${type}-list`);
				});
            } else {
                // Handle deletion error
                console.error(`Error deleting ${type} item. Status: ${response.status}`);
            }
        })
        .catch(error => console.error(`Error deleting ${type} item: ${error}`));
}

// LIST SORTING LOGIC

function sortFct(a, b) {
  return a.sortOrder - b.sortOrder;
}

function reindex(type, data) {
	var i = 0;
	data.forEach(item => {
		if (item.sortOrder != i) {
			item.sortOrder = i;
			saveItem(type, item.id, JSON.stringify(item), true);
		}
		i += 1;
	});
}

function reindexList(sl) {
	type = sl.getAttribute("id").replace("-list","")
	for (let i = 0; i < sl.children.length; i++) {
		let item = getFromCache(type, +sl.children[i].id);
		if (item.sortOrder != i) {
			console.log(item)
			item.sortOrder = i;
			saveItem(type, item.id, JSON.stringify(item), true);
		}
	}
}

const sortableLists = document.getElementsByClassName("sortable");
let draggedItem = null;
let draggingFrom = null;

for (let sortableList of sortableLists) {
	sortableList.addEventListener(
	    "dragstart",
	    (e) => {
	        draggedItem = e.target;
	        draggingFrom = draggedItem.parentElement;
	});
	 
	sortableList.addEventListener(
	    "dragend",
	    (e) => {
	        setTimeout(() => {
	            e.target.style.display = "";
	            draggedItem = null;
	            draggingFrom = null;
	        }, 0);
	});
	 
	sortableList.addEventListener(
	    "dragover",
	    (e) => {
	        if (draggingFrom != sortableList)
	    		return;
	        e.preventDefault();
	    	const afterElement =
	            getDragAfterElement(
	                sortableList,
	                e.clientY);
	        const currentElement =
	            document.querySelector(
	                ".dragging");
	        if (afterElement == null) {
	            sortableList.appendChild(
	                draggedItem
	            );} 
	        else {
	            sortableList.insertBefore(
	                draggedItem,
	                afterElement
	            );}
	        reindexList(sortableList);
	    });
};

const getDragAfterElement = (
    container, y
) => {
    const draggableElements = [
        ...container.querySelectorAll(
            "li:not(.dragging)"
        ),];
 
    return draggableElements.reduce(
        (closest, child) => {
            const box =
                child.getBoundingClientRect();
            const offset =
                y - box.top - box.height / 2;
            if (
                offset < 0 &&
                offset > closest.offset) {
                return {
                    offset: offset,
                    element: child,
                };} 
            else {
                return closest;
            }},
        {
            offset: Number.NEGATIVE_INFINITY,
        }
    ).element;
};

function newShade(hexColor, magnitude) {
    hexColor = hexColor.replace(`#`, ``);
    if (hexColor.length === 6) {
        const decimalColor = parseInt(hexColor, 16);
        let r = (decimalColor >> 16) + magnitude;
        r > 255 && (r = 255);
        r < 0 && (r = 0);
        let g = (decimalColor & 0x0000ff) + magnitude;
        g > 255 && (g = 255);
        g < 0 && (g = 0);
        let b = ((decimalColor >> 8) & 0x00ff) + magnitude;
        b > 255 && (b = 255);
        b < 0 && (b = 0);
        return `#${(g | (b << 8) | (r << 16)).toString(16)}`;
    } else {
        return hexColor;
    }
}

// MOBILE

function toggleSelectedList(type) {
	other = type == "habit" ? "task" : "habit";
	document.getElementById(`${other}s-container`).classList.add("selectedList");
	document.getElementById(`${type}s-container`).classList.remove("selectedList");
}