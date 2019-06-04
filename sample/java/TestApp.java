public class TestApp {
    public static void main(String[] args){
        System.out.println("Test App constructed");
        test()
    }

    private static int test(){
        System.out.println("custom test statement!");
        Car newCar = new Car();
        for(int i = 0; i < 3; i++){
            newCar.drive();
        }
        return 1;
    }
}
