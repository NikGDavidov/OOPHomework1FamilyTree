import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Main {
//    Реализовать, с учетом ооп подхода, приложение
//    Для проведения исследований с генеалогическим древом.
//    Идея: описать некоторое количество компонент, например:
//            - модель человека
//- компонента хранения связей и отношений между людьми: родитель, ребёнок - классика, но можно подумать и про разные отношения
//- компонент для проведения исследований
//- дополнительные компоненты, например отвечающие за вывод данных в консоль, загрузку и сохранения в файл,
//
//    получение\построение отдельных моделей человека.
//    Под “проведением исследования” можно понимать получение всех детей выбранного человека.
//* на первом этапе сложно применять сразу все концепты ООП, упор делается на инкапсуляцию. Если получится продумать иерархию каких-то компонент - здорово. После первой лекции, они не знают про абстракцию и интерфейсы
//
//    Продумать какие проблемы могут возникнуть в том, коде, который они написали.
//            Например, с генеалогическим древом, мы можем знать о двух людях, но не знаем в каких “отношениях” они были - двоюродные или троюродные, или мы точно знаем как звали прапрабабушку, но совершенно не знаем прабабушку - как хранить такие связи или что будет если в компоненту обхода передать ссылку на null-дерево.

        public static void main(String[] args) {
            Person irina = new Person("Irina", "female",56);
            Person vasya = new Person("Vasiliy","male",25);
            Person masha = new Person("Maria","female", 27);
            Person jane = new Person("Jana", "female", 3);
            Person ivan = new Person("Ivan","male",5);
           // Person ekaterina = new Person("Ekaterina")

            GeoTree gt = new GeoTree();
            gt.append(irina, vasya);
            gt.append(irina, masha);
            gt.append(vasya, jane);
            gt.append(vasya, ivan);
          //  gt.append(vasya,ekaterina);

            System.out.println(new Research(gt).spend(irina, Relationship.parent));//выборка детей
            System.out.println(new Research2(gt).byAge(30,'<'));//выборка по возрасту
            System.out.println(new Research3(gt).reAndAge (irina,Relationship.parent, 27,'<'));//сортировка по детям и их возрасту
            FileOutput.fileWrite(new Research2(gt).byAge(40,'<'));// запись выбоорки в файл
}
}

enum Relationship {
    parent,
    children
}

class Person {
    private String fullName;
    private int age;
    private String sex;

    public String getFullName() {
        return fullName;
    }
    public int getAge(){
    return age;
}

    public Person(String fullName, String sex, int age) {
        this.fullName = fullName;
        this.sex = sex;
        this.age = age;
    }
    @Override
    public String toString() {
        return String.format("%s %s %d", this.fullName,this.sex, this.age);
    }
}

class Node {
    public Node(Person p1, Relationship re, Person p2) {
        this.p1 = p1;
        this.re = re;
        this.p2 = p2;
    }

    Person p1;
    Relationship re;
    Person p2;

    @Override
    public String toString() {
        return String.format("<%s %s %s>", p1, re, p2);
    }
}

class GeoTree {
    private ArrayList<Node> tree = new ArrayList<>();

    public ArrayList<Node> getTree() {
        return tree;
    }

    public void append(Person parent, Person children) {
        tree.add(new Node(parent, Relationship.parent, children));
        tree.add(new Node(children, Relationship.children, parent));
    }
}

class Research {
    ArrayList<Node> tree;

    public Research(GeoTree geoTree) {
        tree = geoTree.getTree();
    }

    public ArrayList<Person> spend(Person p, Relationship re) {
        ArrayList<Person> result = new ArrayList<>();
        for (Node t : tree) {
            if (t.p1.getFullName() == p.getFullName() && t.re == re) {
                result.add(t.p2);
            }
        }
        return result;
    }
}

class Research2 extends Research {
    public Research2(GeoTree geoTree) {
        super(geoTree);
    }

    public Set<Person> byAge(int age, char sign) {
        Set<Person> result = new HashSet<>();
        for (Node t : tree) {
            switch (sign) {
                case '>': {
                    if (t.p1.getAge() > age) {
                        result.add(t.p1);
                    }
                    break;
                }
                case '=': {
                    if (t.p1.getAge() == age) {
                        result.add(t.p1);
                    }
                    break;
                }
                case '<': {
                    if (t.p1.getAge() < age) {
                        result.add(t.p1);
                    }
                    break;
                }
            }
        }
        return result;
    }
}
class Research3 extends Research2{
    public Research3(GeoTree geoTree) {
        super(geoTree);
    }
    public Set <Person> reAndAge (Person p, Relationship re, int age, char sign){
        ArrayList <Person> res1 = spend(p, re);
        Set<Person> res2 = byAge(age, sign);
        res2.retainAll(res1);
        return res2;
    }

}

class FileOutput {
    public static void fileWrite(Collection <Person> coll) {
        File output = new File("output.txt");
        try {
            FileWriter writer = new FileWriter(output);
            for (Person p: coll) {
                writer.write(p.toString() + " ; ");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println( e.getMessage());
        }

    }
}