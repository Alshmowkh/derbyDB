package Inversion.verb;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static sarf_package.Utiles.*;

public class UnaugmentedTrilateralVerbInverter {

    public static void main(String[] args) {
        UnaugmentedTrilateralVerbInverter inverter = new UnaugmentedTrilateralVerbInverter();
//        inverter.getConjugation();
//        inverter.ini();
//        inverter.derbyZip();
    }

    private void ini() {
        //بداية من استلام اي كلمة مرشحة تكون فعل
        //تجريد الكلمة من البريفكس والصفكس إن وجد
        // ازالة اللبس للبرفكس من الحالتين حروف مضارعة أو حروف زيادة
        // البريفكس للفعل كل قائمة البريفكس الموجود في الاعلا
        // السفكس للفعل كل الحروف والشكلات الموجودة في القائمة اعلا
        //البحث عن الكلمة المرشحة إن  لم تتعدى الستة الاحرف بعد التجريد في قائمة كل التصريفات التي تبدأ بأول حرف الكلمة المرشحة د
        // في حال المطابقة يتم تتميز الضمير المتصل إن وجد
        // التالي هو استنتاج جذر التصريف المطابق
//       List connectedPro
        ConnectPronouns pro = ConnectPronouns.getInstance();
        List prefixList = pro.get('X');

        List suffix = new ArrayList();
        suffix.addAll(pro.get('P'));
        suffix.addAll(pro.get('N'));
        suffix.addAll(pro.get('A'));
        suffix.addAll(pro.get('E'));
        suffix.addAll(pro.get('I'));
        suffix.addAll(pro.get('M'));

        suffix.stream().distinct().forEach(i -> pl(deDiacritic(i.toString())));
//        pl(ConnectPronouns.getInstance().get('P'));
        pl("");
    }

    private void derbyZip() {
        Properties p=System.getProperties();
        pl(p.getProperty("derby.storage.pageSize"));
    }

   
}
