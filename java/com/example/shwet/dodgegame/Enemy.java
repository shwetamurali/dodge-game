package com.example.shwet.dodgegame;

public class Enemy {
    int x;
        int speed;

        public int getX() {
            return x;
        }
        public void setX(int x) {
            this.x = x;
        }
        public int randomX1(){
            return (int) (Math.random()*325);
        }
        public int randomX2(){
            return (int) (Math.random()*325+325);
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

}
