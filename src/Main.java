class AnimalThread extends Thread{
    private String AnimalName;
    private volatile int meters;
    private volatile boolean finished;
    private int speed;

    public AnimalThread(String name, int priority){
        this.AnimalName = name;
        this.meters = 0;
        this.finished = false;
        this.speed = 100;
        this.setPriority(priority);
        this.setName(name);
    }
    @Override
    public void run(){
        System.out.println(AnimalName + " стартовал, приоритет: "+ this.getPriority()+ " скорость " + speed +" метров ");
        while (meters < 100 && !isInterrupted()){
            meters++;
            if (meters % 10 == 0 ){
                System.out.println(AnimalName + " пробежал " + meters+ " метров, приотритет "+ this.getPriority());
            }
            try {
                int actualSpeed = speed - (this.getPriority() * 5);
                Thread.sleep(Math.max(20, actualSpeed));//задержка 20 мс
            }
            catch (InterruptedException e ){
                System.out.println(AnimalName + " был прерван");
                break;
            }
        }
        finished = true;
        if( meters >= 100){
            System.out.println(AnimalName+ " финишировал ");
        }
    }
    public int getMeters(){
        return meters;
    }
    public String getAnimalName(){
        return AnimalName;
    }
    public boolean isfinished(){
        return  finished;
    }
    public void setAnimalSpeed(int newSpeed){
        this.speed = newSpeed;
    }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("Начало гонки кролика и черепахи");
        AnimalThread rabbit = new AnimalThread("Кролик", Thread.NORM_PRIORITY);
        AnimalThread turtle = new AnimalThread("Черепаха" ,Thread.NORM_PRIORITY);
        rabbit.start();
        turtle.start();
        monitor (rabbit,turtle);
        try {
            rabbit.join();
            turtle.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("=" .repeat(50));
        System.out.println("Гонка закончена");
        printResults(rabbit,turtle);
    }
    private static void monitor (AnimalThread rabbit, AnimalThread turtle){
        int checkCount = 0;
        while (!rabbit.isfinished() || !turtle.isfinished()){
            try {
                Thread.sleep(300);
                int rabbitMeters = rabbit.getMeters();
                int turtleMeters = turtle.getMeters();
                checkCount++;
                System.out.println(" Проверка ");
                System.out.println(" Кролик: "+rabbitMeters +" приоритет " +rabbit.getPriority());
                System.out.println("Черепаха "+turtleMeters+" приоритет " + turtle.getPriority());

                int difference = Math.abs(rabbitMeters - turtleMeters); //условие приоритета
                if(difference >= 25) {
                    if (rabbitMeters > turtleMeters) {
                        rabbit.setPriority(Thread.MIN_PRIORITY);
                        turtle.setPriority(Thread.MAX_PRIORITY);
                        System.out.println(" Черепаха отстает");
                        System.out.println("Понижаем приоритет Кролика до: " + rabbit.getPriority());
                        System.out.println("Повышаем проиритет Черепахи до: " + turtle.getPriority());

                    } else {
                        rabbit.setPriority(Thread.MAX_PRIORITY);
                        turtle.setPriority(Thread.MIN_PRIORITY);
                        System.out.println(" Кролик отстает");
                        System.out.println("Повышаем приоритет Кролика до: " + rabbit.getPriority());
                        System.out.println("Понижаем проиритет Черепахи до: " + turtle.getPriority());
                    }

                }else if (difference >= 15) {
                        if(rabbitMeters > turtleMeters){
                            rabbit.setPriority(Thread.NORM_PRIORITY -1);
                            turtle.setPriority(Thread .NORM_PRIORITY +1);
                            System.out.println("Черепаха отстает, приоритеты скоректированны");
                        }
                        else {
                            rabbit.setPriority(Thread.NORM_PRIORITY +1);
                            turtle.setPriority(Thread .NORM_PRIORITY -1);
                            System.out.println("Кролик отстает, приоритеты скоректированны");
                        }
                } else if (difference <= 5) {
                        rabbit.setPriority(Thread.NORM_PRIORITY);
                        turtle.setPriority(Thread.NORM_PRIORITY);
                        if(checkCount % 3 ==0){
                            System.out.println("Близкая гонка, приоритеты равны");
                        }
                }

                if(rabbitMeters>= 100&& turtleMeters>=100){
                    break;
                }

            }catch (InterruptedException e){
                break;
            }
        }
    }
    private static void printResults(AnimalThread rabbit, AnimalThread turtle){
        System.out.println(" Результаты:");
        System.out.println("Кролик: "+ rabbit.getMeters() +" метров");
        System.out.println("Черепаха: "+ turtle.getMeters() +" метров");

        if(rabbit.getMeters()> turtle.getMeters()){
            System.out.println(" Победитель кролик");
            System.out.println("Отрыв "+ (rabbit.getMeters()-turtle.getMeters())+ " метров");
        } else if (rabbit.getMeters()< turtle.getMeters()) {
            System.out.println(" Победитель черепаха");
            System.out.println("Отрыв "+ (turtle.getMeters()-rabbit.getMeters())+ " метров");
        }
        else {
            System.out.println(" Ничья");
        }
    }
}
