package com.niravramdhanie.twod.game.actions;

/**
 * An action that updates a MultiButtonAction when a button is activated.
 */
public class ButtonUpdateAction implements Action {
    private String buttonId;
    private MultiButtonAction targetAction;
    private boolean isActivated;
    
    /**
     * Creates a new ButtonUpdateAction.
     * 
     * @param buttonId The ID of the button
     * @param targetAction The MultiButtonAction to update
     */
    public ButtonUpdateAction(String buttonId, MultiButtonAction targetAction) {
        this.buttonId = buttonId;
        this.targetAction = targetAction;
        this.isActivated = false;
    }
    
    @Override
    public void execute() {
        // Toggle the activation state
        isActivated = !isActivated;
        
        // Update the target action with the new state
        if (targetAction != null) {
            boolean stateChanged = targetAction.updateButtonState(buttonId, isActivated);
            if (stateChanged) {
                System.out.println("ButtonUpdateAction: Updated button state for " + buttonId + " to " + isActivated);
            }
        }
    }
    
    @Override
    public String getDescription() {
        return "Button update action for " + buttonId + " (currently " + (isActivated ? "active" : "inactive") + ")";
    }
    
    /**
     * Sets the activation state of this button.
     * 
     * @param isActivated Whether the button is activated
     */
    public void setActivated(boolean isActivated) {
        if (this.isActivated != isActivated) {
            this.isActivated = isActivated;
            
            // Update the target action with the new state
            if (targetAction != null) {
                targetAction.updateButtonState(buttonId, isActivated);
            }
        }
    }
    
    /**
     * Gets whether this button is currently activated.
     * 
     * @return True if activated, false otherwise
     */
    public boolean isActivated() {
        return isActivated;
    }
}
