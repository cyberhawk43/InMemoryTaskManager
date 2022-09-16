package test.fp6;


import managment.inMemory.InMemoryTaskManager;
import managment.Managers;

import java.io.IOException;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @Override
    void setManager() {
        manager = new InMemoryTaskManager();
    }
}