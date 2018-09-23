package com.club.minsk.db.fb;

import java.text.SimpleDateFormat;

public class FBUser {
    public String id;
    public String name;
    public String email;
    public String first_name;
    public String last_name;
    public String gender;
    public String birthday;
    public String relationship_status;
    public Picture picture;

    public class Picture{
        public Data data;
        public class Data{
            public Long height;
            public Long width;
            public String url;
        }
    }

    public Integer getSex() {
        int defaultSex = 2;
        if (gender == null)
            return defaultSex;
        if (gender.equals("female"))
            return 1;
        if (gender.equals("male"))
            return 2;
        return defaultSex;
    }

    public Long getUnixBirthday() {
        Long unix_birthday = 0L;
        try {
            unix_birthday = (new SimpleDateFormat("MM/dd/yyyy").parse(birthday).getTime() / 1000);
        } catch (Exception ignore) {
        }
        return unix_birthday;
    }

}
