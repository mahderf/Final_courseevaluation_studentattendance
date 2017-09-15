package com.finalproject.courseevaluation_studentattendance.Controller;

import com.finalproject.courseevaluation_studentattendance.Model.Course;
import com.finalproject.courseevaluation_studentattendance.Model.Evaluation;
import com.finalproject.courseevaluation_studentattendance.Model.Person;
import com.finalproject.courseevaluation_studentattendance.Model.PersonRole;
import com.finalproject.courseevaluation_studentattendance.Repositories.CourseRepository;
import com.finalproject.courseevaluation_studentattendance.Repositories.EvaluationRepository;
import com.finalproject.courseevaluation_studentattendance.Repositories.PersonRepository;
import com.finalproject.courseevaluation_studentattendance.Repositories.RoleRepository;
import com.finalproject.courseevaluation_studentattendance.Services.AttendanceService;
import com.finalproject.courseevaluation_studentattendance.Services.CourseService;
import com.finalproject.courseevaluation_studentattendance.Services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;


@Controller
@RequestMapping("/admin")
public class AdminController {
    //@Autowired
    //AttendanceService attendenceService;

    @Autowired
    PersonService personService;

    @Autowired
    CourseService courseService;

    @Autowired
    PersonRepository personRepo;

    @Autowired
    CourseRepository courseRepo;

    @Autowired
    EvaluationRepository evaluationRepo;
    @Autowired
    RoleRepository roleRepository;





    @RequestMapping("/home")
    public String adminHome(Model model){

        model.addAttribute("allcourses", courseRepo.findAll());
        return "adminpages/adminhome";
    }

    @GetMapping("/addcourse")
    public String addCourse(Model model)
    {
        model.addAttribute("newcourse", new Course());
        return "adminpages/adminaddcourse";
    }
    //
//
    //End date for the course isn't going to be entered here but it will be set when the teacher says the course ended
    @PostMapping("/addcourse")
    public String postCourse(@ModelAttribute("newcourse")Course newcourse)
    {
        courseRepo.save(newcourse);
        return"redirect:/admin/home";
    }
    @GetMapping("/updatecourse/{id}")
    public String editCourse(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("newcourse",courseRepo.findOne(id));

        return "adminpages/adminaddcourse";
    }

    // ===   See the details of the Course
    @GetMapping("/admincoursedetails/{id}")  //id - course id
    public String displayCourse (@PathVariable("id")long id,
                                         Model model) {

        Course currentCourse = courseRepo.findOne(id);
        model.addAttribute("course", currentCourse);

        Person courseInstructor = currentCourse.getInstructor();
        model.addAttribute("courseInstructor", courseInstructor);

        Set<Person> courseStudents = currentCourse.getStudent();
        model.addAttribute("courseStudents", courseStudents);

        return "adminpages/admincoursedetails";
    }

    //need to taste viewing after the team is done with evaluation
    @GetMapping("viewcourseevaluations/{id}")
    public String viewEvaluation(@PathVariable("id") long id, Model model){
        Course thiscourse = courseRepo.findOne(id);
        Iterable<Evaluation> thiscourseevaluation = thiscourse.getEvaluations();
        model.addAttribute("evaluation",thiscourseevaluation);
        model.addAttribute("course",thiscourse);
        return"/adminpages/admincourseevaluation";
    }

    //this will allow the the admin to add an existing student to a course
    // the relationship between student and to courses is many to one so
    //it is not going to let them assign existing students to different courses
    // but if we change the relationship to allow that we can add them to a different
    // courses using this method
    @GetMapping("/addstudenttocourse/{id}")
    public String addStudent(@PathVariable("id") long crsID, Model model)
    {

      PersonRole nrole=roleRepository.findByRoleName("DEFAULT");
       Iterable<Person>students=nrole.getPeople();

        model.addAttribute("students",students);
        model.addAttribute("course",courseRepo.findOne(crsID));


        return "adminpages/admincourseaddstudent";
    }
    @PostMapping("/addstudenttocourse/{crsid}")
    public String studentSavedToCourse(@PathVariable("crsid") long id,
                                       @RequestParam("crs") String crsID,
                                       @ModelAttribute("aStudent") Person p,
                                       Model model)

    {
        Course ncourse=courseRepo.findOne(id);
        ncourse.addStudent(personRepo.findOne(new Long(crsID)));
        courseRepo.save(ncourse);
        return "redirect:/admin/admincoursedetails/"+crsID;
    }

    //this will allow the the admin to assign teachers to a course
    //since a course can only have one teacher every time the teachers chooses
    //different instructor it will reset it which is what we need
    @GetMapping("/addinstructortocourse/{id}")
    public String addTeacher(@PathVariable("id") long crsID, Model model)
    {

        PersonRole nrole=roleRepository.findByRoleName("TEACHER");
        Iterable<Person>teachers=nrole.getPeople();

        model.addAttribute("instructors",teachers);
        model.addAttribute("course",courseRepo.findOne(crsID));


        return "adminpages/admincourseaddinstructor";
    }
    @PostMapping("/addinstructortocourse/{crsid}")
    public String teacherSavedtoCourse(@PathVariable("crsid") long id,
                                       @RequestParam("crs") String crsID,
                                       @ModelAttribute("aInstructor") Person p,
                                       Model model)

    {
        Course ncourse=courseRepo.findOne(id);
        ncourse.setInstructor(personRepo.findOne(new Long(crsID)));
        courseRepo.save(ncourse);
        return "redirect:/admin/admincoursedetails/"+crsID;
    }



    // ===   Remove Course from the list of courses
    @RequestMapping("removecourse/{id}") //Course id
    public String removeCourse (@PathVariable("id")long id, Model model){

        Course course = courseRepo.findOne(id);
        System.out.println("course id to remove " + course.getId());
//        long courseToGoTo = course.getId();

        courseService.removeCourse(course);

        return "redirect:/admin/home"; //" + courseToGoTo;
    }

//    ToDo need to check how it works:
//    // ===   Remove Student from the Course
//    @RequestMapping("course/{courseid}/removestudentfromcourse/{studentid}")
    @RequestMapping("/removestudentfromcourse/{courseid}/{studentid}")
    public String removeStudentFromCourse (@PathVariable("courseid")long courseid,
                                           @PathVariable("studentid")long studentid,
                                           Model model)
    {
        Person student = personService.findById(studentid);

        System.out.println(student.getFirstName());

        Course course = courseRepo.findOne(courseid);
        System.out.println(course.getCourseName());
        courseService.removeStudentFromCourse(course, student);

        String courseIDString = Long.toString(courseid);
        return "rdirect:/admin/admincoursedatails/" + courseIDString;
    }






    // ===   See the List of All People
    @GetMapping("/viewallpeople")
    public String seeAllPeople(Model model)
    {
        Iterable<Person> listOfAllPeople = personRepo.findAll();

        model.addAttribute("listOfAllPeople", listOfAllPeople);
        return "adminpages/viewallpeople";
    }


    // ===   See the List of All Teachers
    @GetMapping("/viewallteachers")
    public String seeAllTeachers(Model model)
    {
        Iterable<Person> listOfAllTeachers = personRepo.findAllByPersonRoles("TEACHER");

        model.addAttribute("listOfAllTeachers", listOfAllTeachers);
        return "adminpages/viewallteachers";
    }


    // ===   See the List of All Students
    @GetMapping("/viewallstudents")
    public String seeAllStudents(Model model)
    {
        Iterable<Person> listOfAllStudents = personRepo.findAllByPersonRoles("DEFAULT");

        model.addAttribute("listOfAllStudents", listOfAllStudents);
        return "adminpages/viewallstudents";
    }

}
