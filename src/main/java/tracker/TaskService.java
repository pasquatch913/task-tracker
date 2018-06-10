package tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public List<TaskEntity> returnTaskForUser(){
        List<TaskEntity> tasks = repository.findByUserId(2).orElseThrow(() -> new EntityNotFoundException());
        return tasks;
    }

}
