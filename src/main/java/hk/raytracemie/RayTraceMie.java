package hk.raytracemie;

public class RayTraceMie {

    public static void main(String[] args) {
        Runnable run1 = () -> {
            System.out.println("Run1 starting");
            RenderMieAtmospheric r1 = new RenderMieAtmospheric();
            r1.generateSimulation();
            System.out.println("Run1 stopped");
        };

        Runnable run2 = () -> {
            System.out.println("Run2 starting");
            RenderMieTransparency r2 = new RenderMieTransparency();
            r2.generateSimulation();
            System.out.println("Run2 stopped");
        };

        Runnable run3 = () -> {
            System.out.println("Run3 starting");
            RenderTransparentRefraction r3 = new RenderTransparentRefraction();
            r3.generateSimulation();
            System.out.println("Run3 stopped");
        };
        
        Thread t1 = new Thread(run1);
        Thread t2 = new Thread(run2);
        Thread t3 = new Thread(run3);
        
        t1.start();
        //t2.start();
        //t3.start();
    }
}
