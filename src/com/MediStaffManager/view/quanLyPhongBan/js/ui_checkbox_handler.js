// In js/ui_checkbox_handler.js
console.log("JS: ui_checkbox_handler.js loaded.");

// This function needs to be callable by table loading functions
function initializeDynamicCheckboxes(tableIdSelector) {
    const selectAllCheckbox = document.getElementById('select-all'); // Assuming one "select-all" for the current active table
    const tableElement = document.querySelector(tableIdSelector);

    if (!tableElement) {
        // console.warn(`JS: Table with selector "${tableIdSelector}" not found for checkbox initialization.`);
        if (selectAllCheckbox) {
             selectAllCheckbox.checked = false;
             selectAllCheckbox.indeterminate = false;
        }
        return;
    }

    const rowCheckboxes = tableElement.querySelectorAll('.row-checkbox');

    const handleSelectAll = (event) => {
        const isChecked = event.target.checked;
        tableElement.querySelectorAll('.row-checkbox').forEach(checkbox => {
            checkbox.checked = isChecked;
        });
    };

    const handleRowCheckboxChange = () => {
        updateSelectAllCheckboxState(tableIdSelector);
    };


    if (selectAllCheckbox) {
        // Clone and replace to remove old listeners effectively before adding new one
        const newSelectAllCheckbox = selectAllCheckbox.cloneNode(true);
        selectAllCheckbox.parentNode.replaceChild(newSelectAllCheckbox, selectAllCheckbox);
        newSelectAllCheckbox.addEventListener('change', handleSelectAll);
    }

    rowCheckboxes.forEach(checkbox => {
        const newCheckbox = checkbox.cloneNode(true);
        checkbox.parentNode.replaceChild(newCheckbox, checkbox);
        newCheckbox.addEventListener('change', handleRowCheckboxChange);
    });

    if (selectAllCheckbox) {
        updateSelectAllCheckboxState(tableIdSelector);
    }
}

function updateSelectAllCheckboxState(tableIdSelector) {
    const selectAllCheckbox = document.getElementById('select-all');
    if (!selectAllCheckbox) return;

    const tableElement = document.querySelector(tableIdSelector);
    if (!tableElement) {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = false;
        return;
    }

    const rowCheckboxes = tableElement.querySelectorAll('.row-checkbox');
    if (rowCheckboxes.length === 0) {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = false;
        return;
    }

    let allChecked = true;
    let noneChecked = true;
    for (let cb of rowCheckboxes) {
        if (cb.checked) {
            noneChecked = false;
        } else {
            allChecked = false;
        }
        if (!allChecked && !noneChecked) break;
    }

    if (allChecked) {
        selectAllCheckbox.checked = true;
        selectAllCheckbox.indeterminate = false;
    } else if (noneChecked) {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = false;
    } else {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = true;
    }
}