// Helper utility functions for modals
console.log("JS: ui_modal_utils.js loaded.");

/**
 * Displays a modal with proper centering
 * @param {HTMLElement} modalElement - The modal element to display
 */
function displayModalCentered(modalElement) {
    if (!modalElement) {
        console.error("JS: Modal element is null or undefined");
        return;
    }
    
    // Make sure to use flex display for proper centering
    modalElement.style.display = 'flex';
    modalElement.style.justifyContent = 'center';
    modalElement.style.alignItems = 'center';
    
    // Make sure any modal content is also properly centered
    const modalContent = modalElement.querySelector('.modal-content');
    if (modalContent) {
        modalContent.style.margin = 'auto';
        
        // Fix position
        modalContent.style.position = 'relative';
        modalContent.style.transform = 'none'; 
    }
}

/**
 * Updates all showModal functions to use flex positioning
 * Should be called once when the page loads
 */
function patchAllModalDisplays() {
    // 1. Override the style.display setter for modal overlays
    // This uses a more effective approach with a MutationObserver
    const modalObserver = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            if (mutation.type === 'attributes' && 
                mutation.attributeName === 'style' && 
                mutation.target.classList && 
                mutation.target.classList.contains('modal-overlay')) {
                
                const style = mutation.target.style;
                
                // If display is being set to block, change it to flex
                if (style.display === 'block') {
                    mutation.target.style.display = 'flex';
                    mutation.target.style.justifyContent = 'center';
                    mutation.target.style.alignItems = 'center';
                    
                    // Fix the content centering too
                    const modalContent = mutation.target.querySelector('.modal-content');
                    if (modalContent) {
                        modalContent.style.margin = 'auto';
                    }
                }
            }
        });
    });
    
    // 2. Find all existing modals and observe them
    document.querySelectorAll('.modal-overlay').forEach(modal => {
        modalObserver.observe(modal, { 
            attributes: true,
            attributeFilter: ['style']
        });
        
        // Apply direct fix to the main modal elements
        modal.style.boxSizing = 'border-box';
        modal.style.padding = '20px';
        
        // Ensure the content inside the modal is centered
        const modalContent = modal.querySelector('.modal-content');
        if (modalContent) {
            modalContent.style.margin = 'auto';
        }
    });
    
    console.log("JS: Modal display functions patched for proper centering");
}

// Helper function to immediately fix all modals in the document
function fixAllModalElements() {
    // Target all modal-overlay elements
    document.querySelectorAll('.modal-overlay').forEach(modal => {
        // Fix for button events
        const closeButtons = modal.querySelectorAll('.close-button, .btn-secondary, button[id*="cancel"], button[id*="Cancel"]');
        closeButtons.forEach(btn => {
            // Preserve existing handlers
            const newBtn = btn.cloneNode(true);
            btn.parentNode.replaceChild(newBtn, btn);
            
            newBtn.addEventListener('click', () => {
                modal.style.display = 'none';
            });
        });
        
        // Fix required CSS for the modal and content
        modal.style.boxSizing = 'border-box';
        modal.style.padding = '20px';
        
        // Ensure style element has transformOrigin correctly set
        const modalContent = modal.querySelector('.modal-content');
        if (modalContent) {
            modalContent.style.margin = 'auto';
            modalContent.style.transformOrigin = 'center center';
        }
    });
    
    console.log("JS: Applied immediate fixes to all modal elements");
}

// Initialize when the document is ready
document.addEventListener('DOMContentLoaded', function() {
    // Patch event handlers and style setters
    patchAllModalDisplays();
    
    // Apply immediate fixes
    setTimeout(fixAllModalElements, 100); // Small delay to ensure DOM is fully processed
});
