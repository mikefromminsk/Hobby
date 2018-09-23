package com.club.minsk.invite_new.invites;

import java.util.List;

public class Group {
    String group_name;
    List<Long> items;

    public Group(String group_name, List<Long> items) {
        this.group_name = group_name;
        this.items = items;
    }
}
