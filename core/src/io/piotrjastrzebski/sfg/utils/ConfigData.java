package io.piotrjastrzebski.sfg.utils;

public class ConfigData {
    private ClampedRangeFloat obstacleDistance;
    private ClampedRangeFloat obstacleGapSize;

    private ClampedRangeInt pickupLives;
    private ClampedRangeInt pickupBoost;
    private ClampedRangeInt pickupShield;
    private ClampedRangeInt pickupToxic;

    private ClampedValueFloat pickupSpawnChance;
    private ClampedValueInt pickupMinSpawnDistance;

    private ClampedValueInt playerInitLives;
    private ClampedValueInt playerInitShields;

    private ClampedValueFloat playerScale;
    private ClampedValueFloat playerCentreOffset;

    private ClampedValueFloat playerFlySpeed;
    private ClampedValueFloat playerFlyMaxSpeed;
    private ClampedValueFloat playerFlyImpulse;

    private ClampedValueFloat playerJumpImpulse;
    private ClampedValueFloat playerJumpDelay;

    private ClampedValueFloat playerDashTime;
    private ClampedValueFloat playerDashDelay;
    private ClampedValueFloat playerDashImpulse;

    private ClampedValueFloat playerLinearDampening;

    private ClampedValueFloat gravity;

    private Config.Difficulty difficulty;
    private Config.Difficulty baseDifficulty;

    public ConfigData(Config.Difficulty difficulty){
        this.difficulty = difficulty;
        obstacleDistance = new ClampedRangeFloat(4, 25, 0.5f);
        obstacleGapSize = new ClampedRangeFloat(6, 25, 0.5f);

        pickupLives = new ClampedRangeInt(0, 10, 1);
        pickupBoost = new ClampedRangeInt(0, 10, 1);
        pickupShield = new ClampedRangeInt(0, 10, 1);
        pickupToxic = new ClampedRangeInt(0, 10, 1);

        pickupSpawnChance = new ClampedValueFloat(0, 1, 0.05f);
        pickupMinSpawnDistance = new ClampedValueInt(0, 16, 1);

        playerInitLives = new ClampedValueInt(1, 99, 1);
        playerInitShields = new ClampedValueInt(1, 99, 1);

        playerScale = new ClampedValueFloat(0.5f, 5.0f, 0.05f);
        playerCentreOffset = new ClampedValueFloat(-8.5f, 8.5f, 0.5f);

        playerFlySpeed = new ClampedValueFloat(1, 20, 0.5f);
        playerFlyMaxSpeed = new ClampedValueFloat(1, 20, 0.5f);
        playerFlyImpulse = new ClampedValueFloat(0, 50, 5);
        playerJumpImpulse = new ClampedValueFloat(4, 64, 1);
        playerJumpDelay = new ClampedValueFloat(0.01f, 1, 0.01f);

        playerDashTime = new ClampedValueFloat(0, 1, 0.05f);
        playerDashDelay = new ClampedValueFloat(0, 16, 0.5f);
        playerDashImpulse = new ClampedValueFloat(0, 160, 5);

        playerLinearDampening = new ClampedValueFloat(0, 1, 0.05f);

        gravity = new ClampedValueFloat(-100, 0, 5);
    }

    public void set(ConfigData data) {
        baseDifficulty = data.getDifficulty();
        obstacleDistance.set(data.getObstacleDistance());
        obstacleGapSize.set(data.getObstacleGapSize());

        pickupLives.set(data.getPickupLives());
        pickupBoost.set(data.getPickupBoost());
        pickupShield.set(data.getPickupShield());
        pickupToxic.set(data.getPickupToxic());

        pickupSpawnChance.set(data.getPickupSpawnChance());
        pickupMinSpawnDistance.set(data.getPickupMinSpawnDistance());

        playerInitLives.set(data.getPlayerInitLives());
        playerInitShields.set(data.getPlayerInitShields());

        playerScale.set(data.getPlayerScale());

        playerCentreOffset.set(data.getPlayerCentreOffset());

        playerFlySpeed.set(data.getPlayerFlySpeed());
        playerFlyMaxSpeed.set(data.getPlayerFlyMaxSpeed());
        playerFlyImpulse.set(data.getPlayerFlyImpulse());

        playerJumpImpulse.set(data.getPlayerJumpImpulse());
        playerJumpDelay.set(data.getPlayerJumpDelay());

        playerDashTime.set(data.getPlayerDashTime());
        playerDashDelay.set(data.getPlayerDashDelay());
        playerDashImpulse.set(data.getPlayerDashImpulse());

        playerLinearDampening.set(data.getPlayerLinearDampening());
        gravity.set(data.getGravity());
    }

    public ClampedRangeFloat getObstacleDistance() {
        return obstacleDistance;
    }

    public ClampedRangeFloat getObstacleGapSize() {
        return obstacleGapSize;
    }

    public ClampedRangeInt getPickupLives() {
        return pickupLives;
    }

    public ClampedRangeInt getPickupBoost() {
        return pickupBoost;
    }

    public ClampedRangeInt getPickupShield() {
        return pickupShield;
    }

    public ClampedRangeInt getPickupToxic() {
        return pickupToxic;
    }

    public Config.Difficulty getDifficulty() {
        return difficulty;
    }

    public Config.Difficulty getBaseDifficulty() {
        return baseDifficulty;
    }

    public ClampedValueFloat getPickupSpawnChance() {
        return pickupSpawnChance;
    }

    public ClampedValueInt getPickupMinSpawnDistance() {
        return pickupMinSpawnDistance;
    }

    public ClampedValueInt getPlayerInitLives() {
        return playerInitLives;
    }

    public ClampedValueInt getPlayerInitShields() {
        return playerInitShields;
    }

    public ClampedValueFloat getPlayerFlySpeed() {
        return playerFlySpeed;
    }

    public ClampedValueFloat getPlayerFlyMaxSpeed() {
        return playerFlyMaxSpeed;
    }

    public ClampedValueFloat getPlayerFlyImpulse() {
        return playerFlyImpulse;
    }

    public ClampedValueFloat getPlayerJumpImpulse() {
        return playerJumpImpulse;
    }

    public ClampedValueFloat getPlayerJumpDelay() {
        return playerJumpDelay;
    }

    public ClampedValueFloat getPlayerDashTime() {
        return playerDashTime;
    }

    public ClampedValueFloat getPlayerDashDelay() {
        return playerDashDelay;
    }

    public ClampedValueFloat getPlayerDashImpulse() {
        return playerDashImpulse;
    }

    public ClampedValueFloat getPlayerLinearDampening() {
        return playerLinearDampening;
    }

    public ClampedValueFloat getGravity() {
        return gravity;
    }

    public ClampedValueFloat getPlayerScale() {
        return playerScale;
    }

    public ClampedValueFloat getPlayerCentreOffset() {
        return playerCentreOffset;
    }

    public void clean() {
        obstacleDistance.clean();
        obstacleGapSize.clean();

        pickupLives.clean();
        pickupBoost.clean();
        pickupShield.clean();
        pickupToxic.clean();

        pickupSpawnChance.clean();
        pickupMinSpawnDistance.clean();
        playerInitLives.clean();
        playerInitShields.clean();
        playerScale.clean();
        playerCentreOffset.clean();

        playerFlySpeed.clean();
        playerFlyMaxSpeed.clean();
        playerFlyImpulse.clean();
        playerJumpImpulse.clean();
        playerJumpDelay.clean();
        playerDashTime.clean();
        playerDashDelay.clean();
        playerDashImpulse.clean();
        playerLinearDampening.clean();
        gravity.clean();
    }

    public void setBaseDifficulty(Config.Difficulty baseDifficulty) {
        this.baseDifficulty = baseDifficulty;
    }
}