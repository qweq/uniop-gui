public class Main {
    public static void main(String[] args) {
        Map map = new Map(40, 40, 100);
        System.out.println(map + "\n");
        int[][] array = map.getFrame(5, 5, 2); // note: zero-indexing
        Point p = new Point(1,1);
        Trajectory t = new Trajectory(map, 25, p);
    }
}

/*  jak teraz zadziałać?
*   pomysł - stworzyć klasę Vehicle która będzie naszym "statkiem powietrznym"
*   wygenerować serię ramek które obrazują zdjęcia zrobione przez SP
*   ^ tylko trzeba zrobić to sensownie, tzn. zacząć od którejś i iść w kierunku końca w sposób "ciągły"
*   działanie programu sprawdzić, licząc potem macierze korelacji z poziomu pojazdu i "prowadząc" go po trajektorii
*   tak jakbysmy jej uprzednio nie znali
*/