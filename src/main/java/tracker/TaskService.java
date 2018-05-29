package tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public TaskEntity returnTask(){
        //System.out.println(repository.findAll());
        Optional<TaskEntity> task = (repository.findById(2));
        //return new TaskDefinition("task1", 7,1);
        return new TaskEntity("task1", 7, 3);
    }

//    public List<TaskEntity> returnAllTasks(){
////        return repository.findAll();
//        return ;
//    }
}
