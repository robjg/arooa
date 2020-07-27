package org.oddjob.arooa.forms;

import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.List;

public class AppleBag {

    private String description;

    private List<Apple> apples = new ArrayList<>();

    private List<AppleBag> moreBags = new ArrayList<>();

    @ArooaText
    public void setDescription(String description) {
        this.description = description;
    }

    public void setApples(int index, Apple apple) {
        new ListSetterHelper<>(apples).set(index, apple);
    }

    @ArooaComponent
    public void setMoreBags(int index, AppleBag appleBag) {
        new ListSetterHelper<>(moreBags).set(index, appleBag);
    }
}
