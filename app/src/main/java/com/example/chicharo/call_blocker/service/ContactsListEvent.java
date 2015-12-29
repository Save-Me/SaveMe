package com.example.chicharo.call_blocker.service;

import com.example.chicharo.call_blocker.ContactModel;

import java.util.List;

/*
* Copyright 2015 Emilio Ruano Noé

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

-NOTICE-
This software makes use of Realm library (https://realm.io/) under the
Apache License, Version 2.0
This program makes use of Butterknife library (https://github.com/JakeWharton/butterknife) under the
Apache License, Version 2.0
*/
public class ContactsListEvent {
    List<ContactModel> contactModels;

    public ContactsListEvent(List<ContactModel> contactModels) {
        this.contactModels = contactModels;
    }

    public List<ContactModel> getContactModels() {
        return contactModels;
    }
}
