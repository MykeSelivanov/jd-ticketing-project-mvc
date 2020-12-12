package com.cybertek.controller;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.enums.Status;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/project")
public class ProjectController {

    ProjectService projectService;
    UserService userService;
    TaskService taskService;

    @Autowired
    public ProjectController(ProjectService projectService, UserService userService, TaskService taskService) {
        this.projectService = projectService;
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping("/create")
    public String createProject(Model model){

        model.addAttribute("project", new ProjectDTO());
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("managers", userService.findManagers());

        return "/project/create";
    }

    @PostMapping("/create")
    public String insertProject(ProjectDTO project){
        projectService.save(project);
        project.setProjectStatus(Status.OPEN);

        return "redirect:/project/create";
    }

    @GetMapping("/delete/{projectcode}")
    public String deleteProject(@PathVariable("projectcode") String projectCode){

        projectService.deleteById(projectCode);
        return "redirect:/project/create";
    }

    @GetMapping("/complete/{projectcode}")
    public String completeProject(@PathVariable("projectcode") String projectCode){

        projectService.complete(projectService.findById(projectCode));
        return "redirect:/project/create";
    }

    @GetMapping("/update/{projectcode}")
    public String editProject(@PathVariable("projectcode") String projectCode, Model model){

        model.addAttribute("project", projectService.findById(projectCode));
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("managers", userService.findManagers());

        return "/project/update";
    }

    @PostMapping("/update/{projectcode}")
    public String updateProject(@PathVariable("projectcode") String projectCode, ProjectDTO project, Model model){

        projectService.update(project);

        return "redirect:/project/create";
    }

    @GetMapping("/manager/complete/{projectcode}")
    public String completeProjectAsManager(@PathVariable("projectcode") String projectCode){

        projectService.complete(projectService.findById(projectCode));
        return "redirect:/project/manager/complete";
    }

    @GetMapping("/manager/complete")
    public String getProjectByManager(Model model){

        UserDTO manager = userService.findById("john@cybertek.com");

        List<ProjectDTO> projects = getCountedListOfProjectDTO(manager);
        model.addAttribute("projects", projects);


        return "/manager/project-status";
    }

    List<ProjectDTO> getCountedListOfProjectDTO(UserDTO manager){
        List<ProjectDTO> list = projectService
                .findAll()
                .stream()
                .filter(project -> project.getAssignedManager().equals(manager))
                .map(project -> {
                    List<TaskDTO> taskList = taskService.findTaskByManager(manager);
                    int completeCount = (int) taskList.stream()
                            .filter(eachTask -> eachTask.getProject().equals(project) && eachTask.getTaskStatus() == Status.COMPLETE).count();
                    int incompleteCount = (int) taskList.stream()
                            .filter(eachTask -> eachTask.getProject().equals(project) && eachTask.getTaskStatus() != Status.COMPLETE).count();

                    project.setCompleteTaskCount(completeCount);
                    project.setUnfinishedTaskCount(incompleteCount);
                    return project;

                }).collect(Collectors.toList());
        return list;
    }

    @GetMapping("/employee/archive")


}
