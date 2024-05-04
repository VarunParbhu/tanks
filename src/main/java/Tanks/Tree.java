package Tanks;

public class Tree {
    private Level level;
    public Integer x;
    public Integer y;

    public Tree (Level level, Integer x, Integer y){
        this.level = level;
        this.x = x;
        this.y = y;
    }

    public void fall (){
        Integer [] height = level.getHeight();
        if(height[x]>y){
            this.y = height[x];
        }
    }
}
