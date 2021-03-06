import model.*;

//import java.util.ArrayList;
//import java.util.Random;

public final class MyStrategy implements Strategy {
    //private final Random random = new Random();
    //public int apprPos;
    int[] xDest = new int[2];
    int[] yDest = new int[2];
    //CellType[] surrounding = new CellType[8];
    CellType[][] cells;
    Trooper[] troopers;

    //TODO write destination change
    @Override
    public void move(Trooper self, World world, Game game, Move move) {
        if (world.getMoveIndex()<=1){
            cells = world.getCells();
            troopers = world.getTroopers();
        }

        int stanceKey;
        if (self.getX()<15&&self.getY()<10){
            xDest[0]=8;
            yDest[0]=15;
        }else if (self.getX()>15&&self.getY()<10){
            xDest[0]=23;
            yDest[0]=15;
        }else if (self.getX()<15&&self.getY()>10){
            xDest[0]=8;
            yDest[0]=5;
        }else{
            xDest[0]=23;
            yDest[0]=5;
        }

        /*
        @code stanceKey goes as movement cost as well
         */
        switch (self.getStance()) {
            case PRONE:{stanceKey = 6;break;}
            case KNEELING:{stanceKey = 4;break;}
            case STANDING:{stanceKey = 2; break;}
            default:{
                throw new IllegalArgumentException("Unsupported stance: " + self.getStance() + '.');
            }
        }

        //TODO write class-specific behavior
        switch (self.getType()){
            //TODO teach medic to come closer to injured teammates
            case FIELD_MEDIC:{
                for (Trooper currTrooper:troopers){
                    if (currTrooper.isTeammate()&&currTrooper.getHitpoints()<75){
                        if (currTrooper.getX()-self.getX()==1&&currTrooper.getY()==self.getY()){
                            move.setDirection(Direction.EAST);
                            while (currTrooper.getHitpoints()<=95||self.getActionPoints()>0){
                                move.setAction(ActionType.HEAL);
                            }
                            }
                        }else if (currTrooper.getX()-self.getX()==-1&&currTrooper.getY()==self.getY()){
                            move.setDirection(Direction.WEST);
                            while (currTrooper.getHitpoints()<=95||self.getActionPoints()>0){
                                move.setAction(ActionType.HEAL);
                            }
                        }else if (currTrooper.getY()-self.getY()==1&&currTrooper.getX()==self.getX()){
                            move.setDirection(Direction.SOUTH);
                            while (currTrooper.getHitpoints()<=95||self.getActionPoints()>0){
                                move.setAction(ActionType.HEAL);
                            }
                        }else if (currTrooper.getY()-self.getY()==-1&&currTrooper.getX()==self.getX()){
                            move.setDirection(Direction.NORTH);
                            while (currTrooper.getHitpoints()<=95||self.getActionPoints()>0){
                                move.setAction(ActionType.HEAL);
                            }
                        }else{
                            if (self.getHitpoints()<=97){
                                move.setDirection(Direction.CURRENT_POINT);
                                while (currTrooper.getHitpoints()<=97||self.getActionPoints()>0){
                                    move.setAction(ActionType.HEAL);
                                }
                            }
                            //Keep calm and stand still
                        }
                    }
                break;
            }
            case SOLDIER:break;
            case COMMANDER:break;
            //We don't need any deeds here, do we?
            case SNIPER:break;
            case SCOUT:break;
            default:{
                throw new IllegalArgumentException("Unsupported trooper type.");
            }

        }

        //Do we really need to set direction here?
        if (self.isHoldingMedikit()&&self.getHitpoints()<=70){
            move.setAction(ActionType.USE_MEDIKIT);
            move.setDirection(Direction.CURRENT_POINT);
        }
        if (self.isHoldingFieldRation()){
            move.setAction(ActionType.EAT_FIELD_RATION);
            move.setDirection(Direction.CURRENT_POINT);
        }

        while (self.getActionPoints() >= stanceKey) {

            if (self.getActionPoints() >= self.getShootCost()) {

                for (Trooper trooper : troopers) {
                    boolean canShoot = world.isVisible(self.getShootingRange(),
                            self.getX(), self.getY(), self.getStance(),
                            trooper.getX(), trooper.getY(), trooper.getStance()
                    );

                    if (canShoot && !trooper.isTeammate()) {
                        if (self.isHoldingGrenade()){
                            move.setAction(ActionType.THROW_GRENADE);
                        }else{
                            move.setAction(ActionType.SHOOT);
                        }
                        move.setX(trooper.getX());
                        move.setY(trooper.getY());
                        return;
                    }
                }
            }

            //TODO movement in case of unpassable terrain and someone standing at the destination
            move.setAction(ActionType.MOVE);
            if ((xDest[0]-self.getX())>(yDest[0]-self.getY())&&xDest[0]-self.getX()<0) {
                if (cells[self.getX()-1][self.getY()]==CellType.FREE) {
                    move.setDirection(Direction.WEST);
                }
            }else if ((xDest[0]-self.getX())>(yDest[0]-self.getY())&&xDest[0]-self.getX()>0){
                if (cells[self.getX()+1][self.getY()]==CellType.FREE) {
                    move.setDirection(Direction.EAST);
                }
            }else if ((xDest[0]-self.getX())<(yDest[0]-self.getY())&&yDest[0]-self.getY()<0){
                if (cells[self.getX()][self.getY()-1]==CellType.FREE) {
                    move.setDirection(Direction.NORTH);
                }else{
                    //Move somewhere else
                }
            }else {
                if (cells[self.getX()][self.getY()+1]==CellType.FREE) {
                    move.setDirection(Direction.SOUTH);
                }
            }
            return;
            // move.setX((int)((xDest[0]-self.getX())/(Math.sqrt((xDest[0]-self.getX())^2+(yDest[0]-self.getY()^2)))));

        }

    }

}

