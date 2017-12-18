public class Main {
    public static void main(String[] args) {
        Map map = new Map(40, 40, 100);
        System.out.println(map + "\n");
        int stepsCount = 25;
        Direction[] dirArray;

        MapFrame array = new MapFrame(map, 5, 5, 2); // note: zero-indexing
        Point p = new Point(5,5);
        Point p1 = new Point(p.getX(), p.getY());
        Trajectory t = new Trajectory(map, stepsCount, p, 2);
        System.out.println("");

        dirArray = p.correlate(map, t.getMapFrames(), p.getX(), p.getY(), 2);
        //System.out.println("p\t" + p1.getX() + "\t" + p1.getY() + "\n\n");
        for (int i = 0; i < stepsCount; i++) {
           p1.move(dirArray[i]);
           System.out.println("w\t" + p1.getX() + "\t" + p1.getY());
        }
    }
}

/*  jak teraz zadziałać?
*   pomysł - stworzyć klasę Vehicle która będzie naszym "statkiem powietrznym"
*   wygenerować serię ramek które obrazują zdjęcia zrobione przez SP
*   ^ tylko trzeba zrobić to sensownie, tzn. zacząć od którejś i iść w kierunku końca w sposób "ciągły"
*   działanie programu sprawdzić, licząc potem macierze korelacji z poziomu pojazdu i "prowadząc" go po trajektorii
*   tak jakbysmy jej uprzednio nie znali
*/