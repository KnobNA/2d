package com.niravramdhanie.twod.game.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.niravramdhanie.twod.game.actions.Action;
import com.niravramdhanie.twod.game.actions.DoorAction;
import com.niravramdhanie.twod.game.actions.TimedAction;
import com.niravramdhanie.twod.game.actions.ToggleAction;
import com.niravramdhanie.twod.game.entity.BallPlayer;
import com.niravramdhanie.twod.game.entity.Box;
import com.niravramdhanie.twod.game.entity.Button;
import com.niravramdhanie.twod.game.entity.Entity;
import com.niravramdhanie.twod.game.entity.Block;

/**
 * Manages the rewind feature for the game.
 * Records player position, timer value, and button states.
 * Replays recorded actions when rewinding.
 */
public class RewindManager {
    // Enum to track rewind state
    public enum RewindState {
        IDLE,       // Not recording or rewinding
        RECORDING,  // Currently recording actions
        REWINDING   // Currently rewinding and replaying actions
    }
    
    // Current state of the rewind system
    private RewindState currentState = RewindState.IDLE;
    
    // Timestamp relative to the start of recording
    private long recordingStartTime;
    private long rewindStartTime;
    
    // Recorded initial state
    private float playerStartX;
    private float playerStartY;
    private int timerStartValue;
    private List<ButtonState> initialButtonStates = new ArrayList<>();
    private Map<String, Boolean> initialToggleStates = new HashMap<>();
    private Map<String, Boolean> initialDoorStates = new HashMap<>();
    
    // Box state tracking
    private List<BoxState> initialBoxStates = new ArrayList<>();
    private List<Box> boxes = new ArrayList<>();
    private Map<Box, List<BoxPositionRecord>> boxPositionHistory = new HashMap<>();
    private List<Block> levelBlocks = new ArrayList<>();
    
    // Recorded actions
    private List<RecordedAction> recordedActions = new ArrayList<>();
    
    // Reference to game objects
    private BallPlayer player;
    private List<Button> buttons;
    private TimerManager timer;
    
    // Map to track toggle states during replay
    private Map<String, Boolean> currentToggleStates = new HashMap<>();
    
    /**
     * Creates a new RewindManager.
     * 
     * @param player The player to record/restore
     * @param buttons The list of buttons to record/restore
     * @param timer The timer to record/restore
     */
    public RewindManager(BallPlayer player, List<Button> buttons, TimerManager timer) {
        this.player = player;
        this.buttons = buttons;
        this.timer = timer;
    }
    
    /**
     * Sets the list of boxes to track for rewind.
     * 
     * @param boxes The list of boxes to track
     */
    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }
    
    /**
     * Sets the level blocks to use for collision detection during rewind.
     * 
     * @param blocks The list of level blocks
     */
    public void setLevelBlocks(List<Block> blocks) {
        this.levelBlocks = new ArrayList<>(blocks);
    }
    
    /**
     * Starts recording player actions and game state.
     */
    public void startRecording() {
        // Clear previous recordings
        recordedActions.clear();
        initialButtonStates.clear();
        initialToggleStates.clear();
        initialDoorStates.clear();
        initialBoxStates.clear();
        currentToggleStates.clear();
        boxPositionHistory.clear();
        
        // Record initial state
        playerStartX = player.getX();
        playerStartY = player.getY();
        timerStartValue = timer.getTime();
        
        // Record initial button states and toggle states
        for (Button button : buttons) {
            String buttonId = getButtonId(button);
            boolean isActivated = button.isActivated();
            
            // Check if the button has a TimedAction and record its state
            boolean hasTimedAction = false;
            boolean timedActionActive = false;
            Action action = button.getAction();
            if (action instanceof TimedAction) {
                hasTimedAction = true;
                timedActionActive = ((TimedAction) action).isActive();
            }
            
            initialButtonStates.add(new ButtonState(buttonId, isActivated, hasTimedAction, timedActionActive));
            
            // Store toggle states for ToggleAction buttons
            if (action instanceof ToggleAction) {
                ToggleAction toggleAction = (ToggleAction) action;
                initialToggleStates.put(buttonId, toggleAction.isToggled());
                
                // Store door states for DoorActions inside ToggleActions
                if (toggleAction.getOnAction() instanceof DoorAction) {
                    DoorAction doorAction = (DoorAction) toggleAction.getOnAction();
                    initialDoorStates.put(doorAction.getDoorId(), doorAction.isDoorOpen());
                }
            } else if (action instanceof DoorAction) {
                DoorAction doorAction = (DoorAction) action;
                initialDoorStates.put(doorAction.getDoorId(), doorAction.isDoorOpen());
            }
        }
        
        // Record initial box states
        for (Box box : boxes) {
            if (box.isActive()) { // Only record active boxes
                initialBoxStates.add(new BoxState(
                    box,
                    box.getX(),
                    box.getY(),
                    box.isBeingCarried(),
                    box.isBeingCarried() ? player : null
                ));
            }
        }
        
        // Set recording state
        currentState = RewindState.RECORDING;
        recordingStartTime = System.currentTimeMillis();
        
        System.out.println("Rewind: Started recording at position (" + playerStartX + ", " + playerStartY + 
                          "), time: " + timerStartValue);
    }
    
    /**
     * Records a button activation.
     * 
     * @param button The button that was activated
     * @param activated Whether the button was activated or deactivated
     */
    public void recordButtonActivation(Button button, boolean activated) {
        if (currentState != RewindState.RECORDING) {
            return;
        }
        
        String buttonId = getButtonId(button);
        long timestamp = System.currentTimeMillis() - recordingStartTime;
        recordedActions.add(new RecordedAction(ActionType.BUTTON_ACTIVATION, timestamp, buttonId, activated));
        
        System.out.println("Rewind: Recorded button " + buttonId + " " + 
                          (activated ? "activation" : "deactivation") + " at time " + timestamp + "ms");
    }
    
    /**
     * Records a box interaction (pick up or drop).
     * 
     * @param box The box that was interacted with
     * @param isPickup Whether the box was picked up (true) or dropped (false)
     */
    public void recordBoxInteraction(Box box, boolean isPickup) {
        if (currentState != RewindState.RECORDING || !box.isActive()) {
            return;
        }
        
        long timestamp = System.currentTimeMillis() - recordingStartTime;
        
        // Record the box position and state at time of interaction
        RecordedAction action = new RecordedAction(
            isPickup ? ActionType.BOX_PICKUP : ActionType.BOX_DROP,
            timestamp,
            null, // No button ID for box interactions
            false // Not used for box interactions
        );
        
        // Store box reference and current position
        action.box = box;
        action.boxX = box.getX();
        action.boxY = box.getY();
        
        recordedActions.add(action);
        
        System.out.println("Rewind: Recorded box " + 
                          (isPickup ? "pickup" : "drop") + " at time " + timestamp + "ms, position (" +
                          box.getX() + ", " + box.getY() + ")");
    }
    
    /**
     * Records the current position of all active boxes.
     * Should be called on each game tick.
     */
    public void recordBoxPositions() {
        if (currentState != RewindState.RECORDING) {
            return;
        }
        
        long timestamp = System.currentTimeMillis() - recordingStartTime;
        
        for (Box box : boxes) {
            if (box.isActive() && box.hasFullRewindTracking()) {
                // Get or create the position history list for this box
                List<BoxPositionRecord> positionHistory = boxPositionHistory.computeIfAbsent(box, k -> new ArrayList<>());
                
                // Record the current position and time
                positionHistory.add(new BoxPositionRecord(
                    timestamp,
                    box.getX(),
                    box.getY(),
                    box.isBeingCarried()
                ));
                
                System.out.println("Recorded box position at time " + timestamp + ": (" + box.getX() + ", " + box.getY() + ")");
            }
        }
    }
    
    /**
     * Starts rewinding the game to the recorded state.
     */
    public void startRewinding() {
        if (currentState != RewindState.RECORDING) {
            return;
        }
        
        // Reset player position
        player.setX(playerStartX);
        player.setY(playerStartY);
        
        // Reset timer
        timer.setTime(timerStartValue);
        
        // Initialize current toggle states with the initial values
        currentToggleStates.clear();
        currentToggleStates.putAll(initialToggleStates);
        
        // Reset button states and toggle states
        for (ButtonState state : initialButtonStates) {
            for (Button button : buttons) {
                String buttonId = getButtonId(button);
                if (buttonId.equals(state.buttonId)) {
                    // Set button activation state without triggering actions
                    button.setActivated(state.activated);
                    
                    // Handle TimedAction states
                    Action action = button.getAction();
                    if (state.hasTimedAction && action instanceof TimedAction) {
                        TimedAction timedAction = (TimedAction) action;
                        timedAction.setActive(state.timedActionActive);
                    }
                    
                    // Handle toggle states using the new method
                    boolean initialToggled = initialToggleStates.getOrDefault(buttonId, false);
                    button.forceSetToggleState(initialToggled);
                    
                    // For actions that aren't toggles but might be DoorActions
                    if (!(action instanceof ToggleAction) && action instanceof DoorAction) {
                        DoorAction doorAction = (DoorAction) action;
                        String doorId = doorAction.getDoorId();
                        boolean initialDoorOpen = initialDoorStates.getOrDefault(doorId, false);
                        doorAction.setDoorOpen(initialDoorOpen);
                    }
                    
                    break;
                }
            }
        }
        
        // Reset box states
        for (BoxState state : initialBoxStates) {
            Box box = state.box;
            box.setX(state.x);
            box.setY(state.y);
            
            // Reset carried state
            if (state.isBeingCarried && state.carrier != null) {
                box.pickUp(state.carrier.getX(), state.carrier.getY(), state.carrier);
            } else if (box.isBeingCarried()) {
                box.drop();
            }
        }
        
        // Mark all actions as not applied
        for (RecordedAction action : recordedActions) {
            action.applied = false;
        }
        
        // Sort actions by timestamp to ensure correct replay order
        Collections.sort(recordedActions, Comparator.comparingLong(a -> a.timestamp));
        
        // Start rewinding
        currentState = RewindState.REWINDING;
        rewindStartTime = System.currentTimeMillis();
        
        System.out.println("Rewind: Started rewinding to position (" + playerStartX + ", " + playerStartY + 
                          "), time: " + timerStartValue);
    }
    
    /**
     * Updates the rewind system.
     * Should be called on each game tick.
     */
    public void update() {
        // If recording, record box positions
        if (currentState == RewindState.RECORDING) {
            recordBoxPositions();
            return;
        }
        
        if (currentState != RewindState.REWINDING) {
            return;
        }
        
        // Calculate current time relative to rewind start
        long currentRelativeTime = System.currentTimeMillis() - rewindStartTime;
        System.out.println("Current rewind time: " + currentRelativeTime);
        
        // Update box positions for boxes with full rewind tracking
        updateBoxPositionsForRewind(currentRelativeTime);
        
        // Apply any actions that should occur at this time
        for (RecordedAction action : recordedActions) {
            if (action.timestamp <= currentRelativeTime && !action.applied) {
                applyAction(action);
                action.applied = true;
            }
        }
        
        // Check if all actions have been applied
        boolean allApplied = true;
        for (RecordedAction action : recordedActions) {
            if (!action.applied) {
                allApplied = false;
                break;
            }
        }
        
        // If all actions are applied, reset rewind state
        if (allApplied) {
            // Sync final states to ensure consistency
            synchronizeFinalStates();
            
            currentState = RewindState.IDLE;
            System.out.println("Rewind: Completed replay of all actions");
        }
    }
    
    /**
     * Updates box positions during rewind based on recorded history.
     * 
     * @param currentRelativeTime The current time relative to the start of rewinding
     */
    private void updateBoxPositionsForRewind(long currentRelativeTime) {
        for (Box box : boxes) {
            if (!box.isActive() || !box.hasFullRewindTracking()) {
                continue; // Skip inactive boxes
            }
            
            List<BoxPositionRecord> positionHistory = boxPositionHistory.get(box);
            if (positionHistory == null || positionHistory.isEmpty()) {
                continue;
            }
            
            // Find the exact position record for the current time
            BoxPositionRecord currentRecord = null;
            
            // Find the record that matches or is closest to our current rewind time
            for (BoxPositionRecord record : positionHistory) {
                if (record.timestamp <= currentRelativeTime) {
                    currentRecord = record;
                } else {
                    break;
                }
            }
            
            // If we're before the first record, use initial state
            if (currentRecord == null) {
                for (BoxState state : initialBoxStates) {
                    if (state.box == box) {
                        box.setX(state.x);
                        box.setY(state.y);
                        System.out.println("Using initial state: (" + state.x + ", " + state.y + ")");
                        break;
                    }
                }
                continue;
            }
            
            // Set the box to the current record's position
            box.setX(currentRecord.x);
            box.setY(currentRecord.y);
            System.out.println("Setting box position at time " + currentRelativeTime + ": (" + currentRecord.x + ", " + currentRecord.y + ")");
        }
    }
    
    /**
     * Synchronizes final states after rewinding to ensure consistency.
     */
    private void synchronizeFinalStates() {
        // Ensure all buttons have their final state
        for (Button button : buttons) {
            String buttonId = getButtonId(button);
            boolean finalToggled = currentToggleStates.getOrDefault(buttonId, false);
            
            // Set final toggle state
            button.forceSetToggleState(finalToggled);
            
            // Update door states via actions
            Action action = button.getAction();
            if (action instanceof DoorAction) {
                DoorAction doorAction = (DoorAction) action;
                doorAction.setDoorOpen(finalToggled);
            } else if (action instanceof ToggleAction) {
                ToggleAction toggleAction = (ToggleAction) action;
                if (toggleAction.getOnAction() instanceof DoorAction) {
                    DoorAction doorAction = (DoorAction) toggleAction.getOnAction();
                    doorAction.setDoorOpen(finalToggled);
                }
                if (toggleAction.getOffAction() instanceof DoorAction) {
                    DoorAction doorAction = (DoorAction) toggleAction.getOffAction();
                    doorAction.setDoorOpen(!finalToggled);
                }
            }
        }
        
        // Ensure boxes are in proper positions
        for (Box box : boxes) {
            if (box.isActive()) {
                // Final position should already be correct through the action replay
                System.out.println("Box final position: (" + box.getX() + ", " + box.getY() + 
                                  "), carried: " + box.isBeingCarried());
            }
        }
    }
    
    /**
     * Applies a recorded action during rewinding.
     * 
     * @param action The action to apply
     */
    private void applyAction(RecordedAction action) {
        switch (action.type) {
            case BUTTON_ACTIVATION:
                applyButtonActivation(action);
                break;
            case BOX_PICKUP:
                applyBoxPickup(action);
                break;
            case BOX_DROP:
                applyBoxDrop(action);
                break;
        }
    }
    
    /**
     * Applies a button activation action during rewinding.
     * 
     * @param action The button activation action to apply
     */
    private void applyButtonActivation(RecordedAction action) {
        for (Button button : buttons) {
            String buttonId = getButtonId(button);
            if (buttonId.equals(action.buttonId)) {
                // Activate or deactivate the button
                if (action.activated) {
                    button.activate();
                } else {
                    button.deactivate();
                }
                
                // Update toggle state if this is a toggle button
                Action buttonAction = button.getAction();
                if (buttonAction instanceof ToggleAction) {
                    boolean newToggled = action.activated ? !currentToggleStates.getOrDefault(buttonId, false) 
                                                         : currentToggleStates.getOrDefault(buttonId, false);
                    currentToggleStates.put(buttonId, newToggled);
                }
                
                System.out.println("Rewind: Applied button " + buttonId + " " + 
                                  (action.activated ? "activation" : "deactivation") + 
                                  " at replay time " + action.timestamp + "ms");
                break;
            }
        }
    }
    
    /**
     * Applies a box pickup action during rewinding.
     * 
     * @param action The box pickup action to apply
     */
    private void applyBoxPickup(RecordedAction action) {
        if (action.box != null) {
            // Set the box position first
            action.box.setX(action.boxX);
            action.box.setY(action.boxY);
            
            // Then pick it up, but don't let it affect position updates during rewind
            action.box.pickUp(player.getX(), player.getY(), player);
            
            // Mark the box as being rewound so it can still be moved
            action.box.setRewinding(true);
            
            System.out.println("Rewind: Applied box pickup at replay time " + 
                              action.timestamp + "ms, position (" + action.boxX + ", " + action.boxY + ")");
        }
    }
    
    /**
     * Applies a box drop action during rewinding.
     * 
     * @param action The box drop action to apply
     */
    private void applyBoxDrop(RecordedAction action) {
        if (action.box != null) {
            // Drop the box
            action.box.drop();
            
            // Set the final position
            action.box.setX(action.boxX);
            action.box.setY(action.boxY);
            
            // Mark the box as no longer being rewound
            action.box.setRewinding(false);
            
            System.out.println("Rewind: Applied box drop at replay time " + 
                              action.timestamp + "ms, position (" + action.boxX + ", " + action.boxY + ")");
        }
    }
    
    /**
     * Toggles the rewind state.
     * If idle, starts recording.
     * If recording, starts rewinding.
     * If rewinding, does nothing.
     */
    public void toggleRewind() {
        switch (currentState) {
            case IDLE:
                startRecording();
                break;
            case RECORDING:
                startRewinding();
                break;
            case REWINDING:
                // Do nothing while rewinding
                break;
        }
    }
    
    /**
     * Gets the current state of the rewind system.
     * 
     * @return The current rewind state
     */
    public RewindState getCurrentState() {
        return currentState;
    }
    
    /**
     * Converts a button to a unique ID for tracking.
     */
    private String getButtonId(Button button) {
        // Use the location as a unique identifier to avoid requiring IDs
        return "button_" + button.getX() + "_" + button.getY();
    }
    
    /**
     * Class to track button state information.
     */
    private static class ButtonState {
        String buttonId;
        boolean activated;
        boolean hasTimedAction;
        boolean timedActionActive;
        
        ButtonState(String buttonId, boolean activated) {
            this.buttonId = buttonId;
            this.activated = activated;
            this.hasTimedAction = false;
            this.timedActionActive = false;
        }
        
        ButtonState(String buttonId, boolean activated, boolean hasTimedAction, boolean timedActionActive) {
            this.buttonId = buttonId;
            this.activated = activated;
            this.hasTimedAction = hasTimedAction;
            this.timedActionActive = timedActionActive;
        }
    }
    
    /**
     * Class to track box state information.
     */
    private static class BoxState {
        Box box;
        float x;
        float y;
        boolean isBeingCarried;
        Entity carrier;
        
        BoxState(Box box, float x, float y, boolean isBeingCarried, Entity carrier) {
            this.box = box;
            this.x = x;
            this.y = y;
            this.isBeingCarried = isBeingCarried;
            this.carrier = carrier;
        }
    }
    
    /**
     * Class to track box position history for rewind.
     */
    private static class BoxPositionRecord {
        long timestamp;
        float x;
        float y;
        boolean isBeingCarried;
        
        BoxPositionRecord(long timestamp, float x, float y, boolean isBeingCarried) {
            this.timestamp = timestamp;
            this.x = x;
            this.y = y;
            this.isBeingCarried = isBeingCarried;
        }
    }
    
    /**
     * Types of recorded actions.
     */
    private enum ActionType {
        BUTTON_ACTIVATION,
        BOX_PICKUP,
        BOX_DROP
    }
    
    /**
     * Class to track information about a recorded action.
     */
    private static class RecordedAction {
        ActionType type;
        long timestamp;
        String buttonId;  // Used for button actions
        boolean activated; // Used for button actions
        boolean applied = false;
        
        // Box action properties
        Box box;          // Used for box actions
        float boxX;       // Used for box actions
        float boxY;       // Used for box actions
        
        RecordedAction(ActionType type, long timestamp, String buttonId, boolean activated) {
            this.type = type;
            this.timestamp = timestamp;
            this.buttonId = buttonId;
            this.activated = activated;
        }
    }
} 