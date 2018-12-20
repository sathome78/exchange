package me.exrates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PropertyEditor {

    public static void main(String[] args) throws IOException {
        InputStream resourceAsStream = PropertyEditor.class.getClassLoader().getResourceAsStream("messages_ko.properties");
        int c = 0;

        assert resourceAsStream != null;
        BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));

        StringBuilder build = new StringBuilder();

        List<String> list = new LinkedList<>();

        boolean isPerenos = false;
        while ((c = in.read()) != -1){
            char ch = (char) c;
            build.append(ch);

            if(String.valueOf(ch).equals("\n") && !isPerenos){
                list.add(build.toString());
                build = new StringBuilder();
            }

            isPerenos = String.valueOf(ch).equals("\\");

        }

        List<String> messagesCodes = new LinkedList<>();


        for (String s : list) {
            try {
                String cod = s.substring(0, s.indexOf("="));
                messagesCodes.add(cod);
            } catch (Exception ignore){}
        }












        InputStream resourceAsStream2 = PropertyEditor.class.getClassLoader().getResourceAsStream("messages_en.properties");
        int c2 = 0;

        assert resourceAsStream2 != null;
        BufferedReader in2 = new BufferedReader(new InputStreamReader(resourceAsStream2, StandardCharsets.UTF_8));

        StringBuilder build2 = new StringBuilder();

        List<String> list2 = new LinkedList<>();

        boolean isPerenos2 = false;
        while ((c2 = in2.read()) != -1){
            char ch2 = (char) c2;
            build2.append(ch2);

            if(String.valueOf(ch2).equals("\n") && !isPerenos2){
                list2.add(build2.toString());
                build2 = new StringBuilder();
            }

            isPerenos2 = String.valueOf(ch2).equals("\\");

        }

        List<String> messagesCodes2 = new LinkedList<>();

        Map<String, String> codeAndFullLine2 = new LinkedHashMap<>();

        for (String s : list2) {
            try {
                String cod = s.substring(0, s.indexOf("="));
                messagesCodes2.add(cod);
                codeAndFullLine2.put(cod, s);
            } catch (Exception ignore){}
        }

//        System.out.println(messagesCodes.contains("message.modal.recomend.turn2fa"));
//        System.out.println(messagesCodes2.contains("message.modal.recomend.turn2fa"));
        for (String messagesCode : messagesCodes2) {
            if(!messagesCodes.contains(messagesCode)){
                System.out.println(codeAndFullLine2.get(messagesCode));
            }
        }
    }

}
