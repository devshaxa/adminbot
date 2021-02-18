package uz.softex.adminbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.softex.adminbot.bot.JobsVNavoiBot;
import uz.softex.adminbot.model.*;
import uz.softex.adminbot.service.*;
import uz.softex.adminbot.util.CreatePost;
import uz.softex.adminbot.util.CreateResume;
import uz.softex.adminbot.util.FileUploadUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin
@Controller
@RequestMapping("/")
public class HomeController {

    private JobService jobService;
    private EmployeeService employeeService;
    private AdvertisementService advertisementService;
    private ResumeService resumeService;
    private JobsVNavoiBot jobsVNavoiBot;
    private PaymentService paymentService;
    private SimpleDateFormat simpleDateFormat;
    private NotificationService notificationService;
    private UserService userService;


    public HomeController(JobService jobService, EmployeeService employeeService, AdvertisementService advertisementService, ResumeService resumeService, JobsVNavoiBot jobsVNavoiBot, PaymentService paymentService, NotificationService notificationService, UserService userService) {
        this.jobService = jobService;
        this.employeeService = employeeService;
        this.advertisementService = advertisementService;
        this.resumeService = resumeService;
        this.jobsVNavoiBot = jobsVNavoiBot;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("jobs", jobService.getByStatus("PAY"));
        model.addAttribute("employees", employeeService.getByStatus("PAY"));
        model.addAttribute("advertisements", advertisementService.getByStatus("PAY"));
        model.addAttribute("resumes", resumeService.getByStatus("PAY"));
        model.addAttribute("payments", paymentService.getAllByDone(simpleDateFormat.format(new Date())));
        return "index";
    }

    @GetMapping("advertisement")
    public String advertisememnt(Model model) {
        model.addAttribute("advertisement", advertisementService.getByStatus("SEND"));
        return "advertisement";
    }

    @PostMapping("advertisement/edit/{id}")
    public String editAdvertisement(@PathVariable("id") Long id, @RequestBody Advertisement advertisement) {
        Advertisement editAdvertisement = advertisementService.get(id);
        editAdvertisement.setText(advertisement.getText());
        advertisementService.save(editAdvertisement);
        return "redirect:/";
    }

    @PostMapping("advertisement/{id}")
    public String acceptAdvertisememnt(@PathVariable Long id, @RequestBody Payment payment) {
        Advertisement advertisement = advertisementService.get(id);
        advertisement.setStatus("PAY");
        advertisementService.save(advertisement);
        Payment paymen1 = new Payment("ADVERTISEMENT", payment.getAmount().doubleValue(), simpleDateFormat.format(new Date()), advertisement.getUser());
        paymen1.setAdvertisement(advertisement);
        paymentService.save(paymen1);
        SendInvoice sendInvoice = new SendInvoice();
        sendInvoice.setChatId(advertisement.getUser().getId().intValue());
        sendInvoice.setCurrency("UZS");
        sendInvoice.setTitle("Reklama uchun to'lov");
        sendInvoice.setDescription("Bir martalik tolov");
        sendInvoice.setPayload("ADVERTISEMENT " + advertisement.getId());
        sendInvoice.setProviderToken("398062629:TEST:999999999_F91D8F69C042267444B74CC0B3C747757EB0E065");
        sendInvoice.setStartParameter("pay");
        List<LabeledPrice> labeledPrices = new ArrayList<>();
        labeledPrices.add(new LabeledPrice("price1", payment.getAmount().intValue() * 100));
        sendInvoice.setPrices(labeledPrices);
        try {
            jobsVNavoiBot.execute(sendInvoice);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return "advertisement";
    }

    @GetMapping("job")
    public String job(Model model) {
        model.addAttribute("jobs", jobService.getByStatus("SEND"));
        return "job";
    }

    @GetMapping("employee")
    public String employee(Model model) {
        model.addAttribute("employees", employeeService.getByStatus("SEND"));
        return "employee";
    }

    @GetMapping("job/get/{id}")
    public String getJob(@PathVariable("id") Long id, Model model) {
        model.addAttribute("emp", employeeService.get(id));
        return "redirect:/";
    }

    @GetMapping("resume")
    public String resume(Model model) {
        model.addAttribute("resumes", resumeService.getByStatus("SEND"));
        return "resume";
    }

    @PostMapping("resume/{id}")
    public String acceptResume(@PathVariable("id") Long id, @RequestBody Payment payment) {
        Resume resume = resumeService.get(id);
        resume.setStatus("PAY");
        resumeService.save(resume);
        Payment paymen1 = new Payment("EMPLOYEE", payment.getAmount().doubleValue(), simpleDateFormat.format(new Date()), resume.getUser());
        paymen1.setResume(resume);
        paymentService.save(paymen1);
        SendInvoice sendInvoice = new SendInvoice();
        sendInvoice.setChatId(resume.getUser().getId().intValue());
        sendInvoice.setCurrency("UZS");
        sendInvoice.setTitle("Rezyumi uchun to'lov");
        sendInvoice.setDescription("Bir martalik tolov");
        sendInvoice.setPayload("RESUME " + resume.getId());
        sendInvoice.setProviderToken("398062629:TEST:999999999_F91D8F69C042267444B74CC0B3C747757EB0E065");
        sendInvoice.setStartParameter("pay");
        List<LabeledPrice> labeledPrices = new ArrayList<>();
        labeledPrices.add(new LabeledPrice("price1", payment.getAmount().intValue() * 100));
        sendInvoice.setPrices(labeledPrices);
        try {
            jobsVNavoiBot.execute(sendInvoice);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return "resume";
    }

    @PostMapping("job/{id}")
    public String acceptJob(@PathVariable("id") Long id, @RequestBody Payment payment) {
        Job job = jobService.get(id);
        job.setStatus("PAY");
        jobService.save(job);
        Payment paymen1 = new Payment("JOB", payment.getAmount().doubleValue(), simpleDateFormat.format(new Date()), job.getUser());
        paymen1.setJob(job);
        paymentService.save(paymen1);
        SendInvoice sendInvoice = new SendInvoice();
        sendInvoice.setChatId(job.getUser().getId().intValue());
        sendInvoice.setCurrency("UZS");
        sendInvoice.setTitle("E'lon uchun to'lov");
        sendInvoice.setDescription("Bir martalik tolov");
        sendInvoice.setPayload("JOB " + job.getId());
        sendInvoice.setProviderToken("398062629:TEST:999999999_F91D8F69C042267444B74CC0B3C747757EB0E065");
        sendInvoice.setStartParameter("pay");
        List<LabeledPrice> labeledPrices = new ArrayList<>();
        labeledPrices.add(new LabeledPrice("price1", payment.getAmount().intValue() * 100));
        sendInvoice.setPrices(labeledPrices);
        try {
            jobsVNavoiBot.execute(sendInvoice);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return "job";
    }

    @PostMapping("employee/{id}")
    public String acceptEmployee(@PathVariable("id") Long id, @RequestBody Payment payment) {
        Employee employee = employeeService.get(id);
        employee.setStatus("PAY");
        employeeService.save(employee);
        Payment paymen1 = new Payment("EMPLOYEE", payment.getAmount().doubleValue(), simpleDateFormat.format(new Date()), employee.getUser());
        paymen1.setEmployee(employee);
        paymentService.save(paymen1);
        SendInvoice sendInvoice = new SendInvoice();
        sendInvoice.setChatId(employee.getUser().getId().intValue());
        sendInvoice.setCurrency("UZS");
        sendInvoice.setTitle("E'lon uchun to'lov");
        sendInvoice.setDescription("Bir martalik tolov");
        sendInvoice.setPayload("EMPLOYEE " + employee.getId());
        sendInvoice.setProviderToken("398062629:TEST:999999999_F91D8F69C042267444B74CC0B3C747757EB0E065");
        sendInvoice.setStartParameter("pay");
        List<LabeledPrice> labeledPrices = new ArrayList<>();
        labeledPrices.add(new LabeledPrice("price1", payment.getAmount().intValue() * 100));
        sendInvoice.setPrices(labeledPrices);
        try {
            jobsVNavoiBot.execute(sendInvoice);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return "employee";
    }

    @PostMapping("job/edit/{id}")
    public String editJob(@PathVariable("id") Long id, @RequestBody Job job) {
        Job editJob = jobService.findById(id);
        editJob.setWorkingHours(job.getWorkingHours());
        editJob.setPlace(job.getPlace());
        if (!editJob.getPosition().equals(job.getPosition())) {
            editJob.setPosition(job.getPosition());
            CreatePost.generateJob(editJob);
        }
        editJob.setSalary(job.getSalary());
        editJob.setResponsible(job.getResponsible());
        editJob.setDemands(job.getDemands());
        editJob.setContact(job.getContact());
        editJob.setAdditional(job.getAdditional());
        jobService.save(job);
        return "redirect:/";
    }

    @PostMapping("employee/edit/{id}")
    public String editEmployee(@PathVariable("id") Long id, @RequestBody Employee employee) throws IOException {
        Employee editEmployee = employeeService.get(id);
        editEmployee.setName(employee.getName());
        editEmployee.setAge(employee.getAge());
        editEmployee.setEducation(employee.getEducation());
        editEmployee.setSalary(employee.getSalary());
        editEmployee.setPhone(employee.getPhone());
        editEmployee.setExperience(employee.getExperience());
        editEmployee.setAdditional(employee.getAdditional());
        employeeService.save(editEmployee);
        return "redirect:/";
    }

    @PostMapping("employee/edit/pic/{id}")
    public String editEmployee(@RequestParam(required = false) MultipartFile file,
                               @PathVariable("id") Long id) throws IOException {
        if (file != null) {
            Employee editEmployee = employeeService.get(id);
            FileUploadUtil.saveFile("pic/", editEmployee.getPicUrl(), file);
            employeeService.save(editEmployee);
        }
        return "redirect:/";
    }

    @PostMapping("resume/edit/{id}")
    public String editResume(@PathVariable("id") Long id, @RequestBody Resume resume) {
        Resume editResume = resumeService.get(id);
        editResume.setFirstname(resume.getFirstname());
        editResume.setLastname(resume.getLastname());
        editResume.setFathersName(resume.getFathersName());
        editResume.setAddress(resume.getAddress());
        editResume.setMaritalStatus(resume.getMaritalStatus());
        editResume.setEmail(resume.getEmail());
        editResume.setDateOfBirth(resume.getDateOfBirth());
        editResume.setPhone(resume.getPhone());
        editResume.setLanguage(resume.getLanguage());
        editResume.setJobExperience(resume.getJobExperience());
        editResume.setEducation(resume.getEducation());
        editResume.setPurpose(resume.getPurpose());
        editResume.setPersonalProperty(resume.getPersonalProperty());
        editResume.setAdditionalKnowledge(resume.getAdditionalKnowledge());
        resumeService.save(editResume);
        return "redirect:/";
    }

    @PostMapping("advertisement/pay/{id}")
    public String payForAdvertisement(@PathVariable("id") Long id) {
        Advertisement advertisement = advertisementService.get(id);
        advertisement.setStatus("DONE");
        advertisementService.save(advertisement);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(-1001389126998l);
        sendPhoto.setCaption(advertisement.getText());
        sendPhoto.setPhoto(new File("pic/" + advertisement.getPicUrl()));
        try {
            jobsVNavoiBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        Payment payment = paymentService.findByAdvertisementId(advertisement.getId());
        payment.setPayDate(simpleDateFormat.format(new Date()));
        paymentService.save(payment);
        notificationService.notify(new Notification("payment", payment), "UserA");
        return "redirect:/";
    }

    @PostMapping("resume/pay/{id}")
    public String payForResume(@PathVariable("id") Long id) {
        Resume resume = resumeService.get(id);
        resume.setStatus("DONE");
        resumeService.save(resume);
        Payment payment = paymentService.findByResumeId(resume.getId());
        payment.setPayDate(simpleDateFormat.format(new Date()));
        paymentService.save(payment);
        notificationService.notify(new Notification("payment", payment), "UserA");
        SendDocument sendDocument = new SendDocument();
        sendDocument.setDocument(CreateResume.printPdf(resume));
        sendDocument.setChatId(resume.getUser().getId());
        try {
            jobsVNavoiBot.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @PostMapping("employee/pay/{id}")
    public String payForEmployee(@PathVariable("id") Long id) {
        Employee employee = employeeService.get(id);
        employee.setStatus("DONE");
        employeeService.save(employee);
        Payment payment = paymentService.findByEmployeeId(employee.getId());
        payment.setPayDate(simpleDateFormat.format(new Date()));
        paymentService.save(payment);
        notificationService.notify(new Notification("payment", payment), "UserA");
        Boolean isFemale = employee.getName().matches("(.*)va (.*)");
        String pic = isFemale ? "\uD83D\uDC69\u200D\uD83D\uDCBC" : "\uD83D\uDC68\u200D\uD83D\uDCBC";
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(-1001389126998l);
        sendPhoto.setParseMode("Markdown");
        sendPhoto.setCaption("Ish kerak\n\n" +
                pic + "*Xodim:* " + employee.getName() + "\n" +
                "\uD83D\uDD51 *Yoshi:* " + employee.getAge() + "\n" +
                "\uD83D\uDCDA *Bilim va tajribalari:* " + employee.getExperience() + "\n" +
                "\uD83D\uDCB0 *Maoshi:* " + employee.getSalary() + "\n" +
                "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB *Ma'lumoti:* " + employee.getEducation() + "\n" +
                "‼️ *Qo'shimcha ma'lumotlar:* " + employee.getAdditional() + "\n" +
                "\uD83D\uDCF1 *Telegram:* [" + employee.getUser().getFirstname() + "](tg://user?id=" + employee.getUser().getId() + ")\n" +
                "\uD83D\uDCDE *Telefon:* " + employee.getPhone());
        sendPhoto.setPhoto(new File("pic/" + employee.getPicUrl()));
        try {
            jobsVNavoiBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @PostMapping("job/pay/{id}")
    public String payForJob(@PathVariable("id") Long id) {
        Job job = jobService.get(id);
        job.setStatus("DONE");
        jobService.save(job);
        Payment payment = paymentService.findByJobId(job.getId());
        payment.setPayDate(simpleDateFormat.format(new Date()));
        paymentService.save(payment);
        notificationService.notify(new Notification("payment", payment), "UserA");
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(-1001389126998l);
        sendPhoto.setPhoto(new File("pic/" + job.getPicUrl()));
        sendPhoto.setParseMode("MArkdown");
        sendPhoto.setCaption("Xodim kerak\n\n" +
                "\uD83C\uDFE2 *Ish joyi:* " + job.getPlace() + "\n" +
                "\uD83D\uDD51 *Lavozim:* " + job.getPosition() + "\n" +
                "\uD83D\uDCDA *Talablar:* " + job.getDemands() + "\n" +
                "\uD83D\uDCB0 *Maoshi:* " + job.getSalary() + "\n" +
                "\uD83D\uDD52 *Ish vaqti:* " + job.getWorkingHours() + "\n" +
                "\uD83D\uDCF1 *Telegram:* [" + job.getUser().getFirstname() + "](tg://user?id=" + job.getUser().getId() + ")\n" +
                "\uD83D\uDCDE *Aloqa:* " + job.getContact() + "\n" +
                "‼️ *Qo'shimcha ma'lumotlar:* " + job.getAdditional() + "\n" +
                "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB *Mas'ul:* " + job.getResponsible());
        try {
            jobsVNavoiBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @PostMapping("advertisement/delete/{id}")
    public String deleteAdvertisement(@PathVariable("id") Long id) {
        advertisementService.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("employee/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("job/delete/{id}")
    public String deleteJob(@PathVariable("id") Long id) {
        jobService.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("resume/delete/{id}")
    public String deleteResume(@PathVariable("id") Long id) {
        resumeService.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("table")
    public String table() {
        return "tables";
    }

    @GetMapping("message")
    public String message(Message message) {
        return "message";
    }

    @PostMapping("message/send")
    public String sendMessage(@RequestBody Message message) {
        if (message.getImgUrl() == null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(message.getText());
            sendMessage.enableMarkdown(true);
            sendMessage.disableWebPagePreview();
            userService.findAll().forEach(user -> {
                sendMessage.setChatId(user.getId());
                try {
                    jobsVNavoiBot.execute(sendMessage);
                    Thread.sleep(1000);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } else {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(new File("pic/"+message.getImgUrl()));
            sendPhoto.setCaption(message.getText());
            sendPhoto.setParseMode("Markdown");
            userService.findAll().forEach(user -> {
                try {
                    System.out.println(user.getFirstname());
                    sendPhoto.setChatId(user.getId());
                    jobsVNavoiBot.execute(sendPhoto);
                    Thread.sleep(1000);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        return "redirect:/";
    }

}
