package uz.softex.adminbot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.softex.adminbot.model.*;
import uz.softex.adminbot.service.*;
import uz.softex.adminbot.util.CreatePost;
import uz.softex.adminbot.util.CreateResume;


import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JobsVNavoiBot extends TelegramLongPollingBot {

    private JobService jobService;
    private UserService userService;
    private AdvertisementService advertisementService;
    private EmployeeService employeeService;
    private SessionService sessionService;
    private ResumeService resumeService;
    private SimpleDateFormat simpleDateFormat;
    private PaymentService paymentService;

    @Autowired
    private NotificationService notificationService;

    public JobsVNavoiBot(JobService jobService, UserService userService, AdvertisementService advertisementService, EmployeeService employeeService, SessionService sessionService, ResumeService resumeService, PaymentService paymentService) {
        this.jobService = jobService;
        this.userService = userService;
        this.advertisementService = advertisementService;
        this.employeeService = employeeService;
        this.sessionService = sessionService;
        this.resumeService = resumeService;
        this.paymentService = paymentService;
        this.simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public String getBotToken() {
        return "1382475529:AAEfafGgpGxd3SGLp8DtS8D6kkKjpmmlUT4";
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = null;
        String messageText = "";
        Long userId = null;
        Long chatId = null;
        User currentUser = null;
        uz.softex.adminbot.model.User user;
        Session session;
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                messageText = update.getMessage().getText();
            }
            if (update.getMessage().hasContact()) {
                messageText = update.getMessage().getContact().getPhoneNumber();
            }
            if(update.getMessage().hasSuccessfulPayment()){
                SuccessfulPayment successfulPayment = update.getMessage().getSuccessfulPayment();
                String [] data = successfulPayment.getInvoicePayload().split(" ");
                if(data[0].equals("ADVERTISEMENT")){
                    Advertisement advertisement = advertisementService.get(Long.parseLong(data[1]));
                    advertisement.setStatus("DONE");
                    advertisementService.save(advertisement);
                    Payment payment = paymentService.findByAdvertisementId(advertisement.getId());
                    payment.setPayDate(simpleDateFormat.format(new Date()));
                    paymentService.save(payment);
                    sendMessage(update.getMessage().getChatId(),"To'lov muvaffaqqiyatli amalga oshirildi!");
                    sendImage(-1001389126998l,advertisement.getText(),advertisement.getPicUrl(),"ADVERTISEMENT");
                } else if(data[0].equals("RESUME")){
                    Resume resume = resumeService.get(Long.parseLong(data[1]));
                    resume.setStatus("DONE");
                    resumeService.save(resume);
                    Payment payment = paymentService.findByResumeId(resume.getId());
                    payment.setPayDate(simpleDateFormat.format(new Date()));
                    paymentService.save(payment);
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(update.getMessage().getChatId());
                    sendDocument.setDocument(CreateResume.printPdf(resume));
                    try {
                        sendMessage(update.getMessage().getChatId(),"To'lov muvaffaqqiyatli amalga oshirildi!");
                        execute(sendDocument);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if(data[0].equals("JOB")){
                    Job job = jobService.get(Long.parseLong(data[1]));
                    job.setStatus("DONE");
                    jobService.save(job);
                    Payment payment = paymentService.findByJobId(job.getId());
                    payment.setPayDate(simpleDateFormat.format(new Date()));
                    paymentService.save(payment);
                    sendMessage(update.getMessage().getChatId(),"To'lov muvaffaqqiyatli amalga oshirildi!");
                    sendImage(-1001389126998l,"Xodim kerak\n\n" +
                            "\uD83C\uDFE2 *Ish joyi:* " + job.getPlace() + "\n" +
                            "\uD83D\uDD51 *Lavozim:* " + job.getPosition() + "\n" +
                            "\uD83D\uDCDA *Talablar:* " + job.getDemands() + "\n" +
                            "\uD83D\uDCB0 *Maoshi:* " + job.getSalary() + "\n" +
                            "\uD83D\uDD52 *Ish vaqti:* "+job.getWorkingHours()+"\n"+
                            "\uD83D\uDCF1 *Telegram:* [" + update.getMessage().getFrom().getFirstName() + "](tg://user?id=" + update.getMessage().getFrom().getId() + ")\n" +
                            "\uD83D\uDCDE *Aloqa:* " + job.getContact()+"\n" +
                            "‼️ *Qo'shimcha ma'lumotlar:* " + job.getAdditional()+"\n" +
                            "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB *Mas'ul:* " + job.getResponsible(),job.getPicUrl(),"JOB");
                } else if(data[0].equals("EMPLOYEE")){
                    Employee employee = employeeService.get(Long.parseLong(data[1]));
                    employee.setStatus("DONE");
                    employeeService.save(employee);
                    Payment payment = paymentService.findByEmployeeId(employee.getId());
                    payment.setPayDate(simpleDateFormat.format(new Date()));
                    paymentService.save(payment);
                    sendMessage(update.getMessage().getChatId(),"To'lov muvaffaqqiyatli amalga oshirildi!");
                    Boolean isFemale = employee.getName().matches("(.*)va (.*)");
                    String pic = isFemale ? "\uD83D\uDC69\u200D\uD83D\uDCBC" : "\uD83D\uDC68\u200D\uD83D\uDCBC";
                    sendImage(-1001389126998l, "Ish kerak\n\n" +
                            pic + "*Xodim:* " + employee.getName() + "\n" +
                            "\uD83D\uDD51 *Yoshi:* " + employee.getAge() + "\n" +
                            "\uD83D\uDCDA *Bilim va tajribalari:* " + employee.getExperience() + "\n" +
                            "\uD83D\uDCB0 *Maoshi:* " + employee.getSalary() + "\n" +
                            "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB *Ma'lumoti:* " + employee.getEducation() + "\n" +
                            "‼️ *Qo'shimcha ma'lumotlar:* " + employee.getAdditional() + "\n" +
                            "\uD83D\uDCF1 *Telegram:* [" + employee.getUser().getFirstname() + "](tg://user?id=" + employee.getUser().getId() + ")\n" +
                            "\uD83D\uDCDE *Telefon:* " + employee.getPhone(), employee.getPicUrl(), "EMPLOYEE");
                }
                Payment payment = new Payment(data[0],10000d, simpleDateFormat.format(new Date()), userService.getById(update.getMessage().getFrom().getId().longValue()));
                paymentService.save(payment);
            }
            message = update.getMessage();
            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId().longValue();
            currentUser = update.getMessage().getFrom();
        }

        if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
            messageText = update.getCallbackQuery().getData();
            userId = update.getCallbackQuery().getFrom().getId().longValue();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            currentUser = update.getCallbackQuery().getFrom();
        }

        if(update.hasPreCheckoutQuery()){
            System.out.println(update.getPreCheckoutQuery().getFrom());
            AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery();
            answerPreCheckoutQuery.setPreCheckoutQueryId(update.getPreCheckoutQuery().getId());
            answerPreCheckoutQuery.setOk(true);
            try {
                execute(answerPreCheckoutQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


        if (!userService.exist(userId)) {
            userService.save(new uz.softex.adminbot.model.User(userId,
                    currentUser.getUserName(),
                    currentUser.getFirstName(),
                    currentUser.getLastName()));
            sessionService.save(new Session(new Job(), new Employee(), new Advertisement(), new Handle("", "lang", 1), new Resume(), userService.getById(userId)));
        }

        user = userService.getById(userId);
        session = sessionService.getById(userId);

        /******* Button parametrs **********/

        if (messageText.equals("/start")) {
            Handle handle = session.getHandle();
            handle.setMenuName("start");
            handle.setStep(1);
            session.setHandle(handle);
            sessionService.update(session);

        } else if (messageText.equals("\uD83C\uDFE2 Ish kerak") || messageText.equals("\uD83C\uDFE2 Нужна работа")) {
            Handle handle = session.getHandle();
            handle.setMenuName("employee");
            handle.setStep(1);
            session.setHandle(handle);
            sessionService.update(session);
        } else if (messageText.equals("\uD83D\uDCD1 Rezyumi shakllantirish") || messageText.equals("\uD83D\uDCD1 Написать резюме")) {
            Handle handle = session.getHandle();
            handle.setMenuName("resume");
            handle.setStep(1);
            session.setHandle(handle);
            sessionService.update(session);
        } else if (messageText.equals("\uD83D\uDC68\u200D\uD83D\uDCBB Xodim kerak") || messageText.equals("\uD83D\uDC68\u200D\uD83D\uDCBB Нужен сотрудник")) {
            Handle handle = session.getHandle();
            handle.setMenuName("job");
            handle.setStep(1);
            session.setHandle(handle);
            sessionService.update(session);

        } else if (messageText.equals("\uD83D\uDDBC Reklama berish") || messageText.equals("\uD83D\uDDBC Реклама")) {

            Handle handle = session.getHandle();
            handle.setMenuName("advertisement");
            handle.setStep(1);
            session.setHandle(handle);
            sessionService.update(session);

        } else if (messageText.equals("\uD83D\uDEAB Bekor qilish") || messageText.equals("\uD83D\uDEAB Отмена")) {
            Handle handle = session.getHandle();
            handle.setStep(1);
            handle.setMenuName("main");
            session.setHandle(handle);
            sessionService.update(session);
        } else if (messageText.equals("⬅️ Назад") || messageText.equals("⬅️ Orqaga")) {
            Handle handle = session.getHandle();
            if (session.getHandle().getStep() == 2) {
                handle.setStep(1);
                handle.setMenuName("main");
            } else {
                handle.setStep(handle.getStep() - 2);
            }
        } else if (messageText.equals("⚙️Sozlash") || messageText.equals("⚙️Настройки")) {
            Handle handle = session.getHandle();
            handle.setStep(1);
            handle.setMenuName("setting");
            session.setHandle(handle);
            sessionService.update(session);
        } else if (messageText.equals("\uD83C\uDF0E Tilni o'zgartirish") || messageText.equals("\uD83C\uDF0E Изменить язык")) {
            Handle handle = session.getHandle();
            handle.setStep(1);
            handle.setMenuName("lang");
            session.setHandle(handle);
            sessionService.update(session);
        } else if (messageText.equals("⬅️ На главную") || messageText.equals("⬅️ Bosh menyuga qaytish")) {
            Handle handle = session.getHandle();
            handle.setMenuName("main");
            handle.setStep(1);
            session.setHandle(handle);
            sessionService.update(session);
        } else if (messageText.equals("ℹ️ Biz haqimizda") || messageText.equals("ℹ️ О нас")) {
            Handle handle = session.getHandle();
            handle.setMenuName("info");
            session.setHandle(handle);
        }

        /******* Button parametrs *********/

        /////////////////////////////////////////////////////////////////////////////////////////////////////////

        /******* Input parametrs **********/

        else if ((session.getHandle().getMenuName().equals("start") && session.getHandle().getStep() == 2) || session.getHandle().getMenuName().equals("lang")) {
            if (messageText.equals("uz")) {
                Handle handle = session.getHandle();
                handle.setLang("uz");
                handle.setMenuName("main");
                handle.setStep(1);
                session.setHandle(handle);
                sessionService.update(session);
            } else if (messageText.equals("ru")) {
                Handle handle = session.getHandle();
                handle.setLang("ru");
                handle.setMenuName("main");
                handle.setStep(1);
                session.setHandle(handle);
                sessionService.update(session);
            }
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (session.getHandle().getMenuName().equals("employee")) {
            switch (session.getHandle().getStep()) {
                case 2: {
                    if (message.hasPhoto()) {
                        List<PhotoSize> photos = update.getMessage().getPhoto();
                        PhotoSize photo = photos.get(photos.size() - 1);
                        String id = photo.getFileId();
                        try {
                            GetFile getFile = new GetFile();
                            getFile.setFileId(id);
                            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                            String filePath = file.getFilePath();
                            URL url = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath);
                            InputStream in = url.openStream();
                            File file1 = new File("pic/" + file.getFileId() + ".jpg");
                            String path = file1.getCanonicalPath().replace("\\", "/");
                            OutputStream out = new BufferedOutputStream(new FileOutputStream("pic/" + file.getFileId() + ".jpg"));
                            for (int b; (b = in.read()) != -1; ) {
                                out.write(b);
                            }
                            Employee employee = session.getEmployee();
                            employee.setPicUrl(file.getFileId() + ".jpg");
                            session.setEmployee(employee);
                            sessionService.update(session);
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(1);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;
                case 3: {
                    Employee employee = session.getEmployee();
                    employee.setName(messageText);
                    session.setEmployee(employee);
                    sessionService.update(session);
                }
                break;
                case 4: {
                    try {
                        Employee employee = session.getEmployee();
                        employee.setAge(Integer.parseInt(messageText));
                        session.setEmployee(employee);
                        sessionService.update(session);
                    } catch (NumberFormatException e) {
                        Handle handle = session.getHandle();
                        handle.setStep(3);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;
                case 5: {
                    Employee employee = session.getEmployee();
                    employee.setExperience(messageText);
                    session.setEmployee(employee);
                    sessionService.update(session);
                }
                break;
                case 6: {
                    try {
                        Employee employee = session.getEmployee();
                        employee.setSalary(Double.valueOf(messageText));
                        session.setEmployee(employee);
                        sessionService.update(session);
                    } catch (NumberFormatException e) {
                        Handle handle = session.getHandle();
                        handle.setStep(5);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;
                case 7: {
                    Employee employee = session.getEmployee();
                    employee.setEducation(messageText);
                    session.setEmployee(employee);
                    sessionService.update(session);
                }
                break;
                case 8: {
                    Employee employee = session.getEmployee();
                    employee.setAdditional(messageText);
                    session.setEmployee(employee);
                    sessionService.update(session);
                }
                break;
                case 9: {
                    String phone = getNumber(messageText);
                    if (phone != null) {
                        Employee employee = session.getEmployee();
                        employee.setPhone(messageText);
                        session.setEmployee(employee);
                        sessionService.update(session);
                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(8);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;
                case 10: {
                    if (messageText.equals("accept")) {
                        Employee employee = session.getEmployee();
                        Employee createEmployee = new Employee();
                        Handle handle = session.getHandle();
                        handle.setStep(1);
                        handle.setMenuName("main");
                        sessionService.update(session);


                        createEmployee.setAdditional(employee.getAdditional());
                        createEmployee.setAge(employee.getAge());
                        createEmployee.setEducation(employee.getEducation());
                        createEmployee.setExperience(employee.getExperience());
                        createEmployee.setName(employee.getName());
                        createEmployee.setPhone(employee.getPhone());
                        createEmployee.setPicUrl(employee.getPicUrl());
                        createEmployee.setSalary(employee.getSalary());
                        createEmployee.setUser(session.getUser());
                        createEmployee.setStatus("SEND");
                        createEmployee.setDate(simpleDateFormat.format(new Date()));
                        createEmployee = employeeService.save(createEmployee);
                        notificationService.notify(new Notification("hodim",createEmployee),"UserA");
                        sendMessage(chatId, handle.getLang().equals("uz") ? "Ma'lumotlar muvaffaqiyatli joylashtirildi.\nSo'rovingiz admin tomonidan ko'rib chiqilib xabar yuboriladi." : "Данные были успешно размещены");
                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(9);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
            }
        } else if (session.getHandle().getMenuName().equals("job")) {
            switch (session.getHandle().getStep()) {
                case 2: {
                    if (message.hasPhoto()) {
                        List<PhotoSize> photos = update.getMessage().getPhoto();
                        PhotoSize photo = photos.get(photos.size() - 1);
                        String id = photo.getFileId();
                        try {
                            GetFile getFile = new GetFile();
                            getFile.setFileId(id);
                            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                            String filePath = file.getFilePath();
                            URL url = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath);
                            InputStream in = url.openStream();
                            File file1 = new File("pic/" + file.getFileId() + ".jpg");
                            String path = file1.getCanonicalPath().replace("\\", "/");
                            OutputStream out = new BufferedOutputStream(new FileOutputStream("pic/" + file.getFileId() + ".jpg"));
                            for (int b; (b = in.read()) != -1; ) {
                                out.write(b);
                            }
                            Job job = session.getJob();
                            job.setPicUrl(file.getFileId() + ".jpg");
                            session.setJob(job);
                            sessionService.update(session);
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(1);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;
                case 3: {
                    Job job = session.getJob();
                    job.setPlace(messageText);
                    session.setJob(job);
                    sessionService.update(session);
                }
                break;
                case 4: {
                    Job job = session.getJob();
                    job.setPosition(messageText);
                    session.setJob(job);
                    sessionService.update(session);
                }
                break;
                case 5: {
                    Job job = session.getJob();
                    job.setDemands(messageText);
                    session.setJob(job);
                    sessionService.update(session);
                }
                break;
                case 6: {
                    try {
                        Job job = session.getJob();
                        job.setSalary(Double.valueOf(messageText));
                        session.setJob(job);
                        sessionService.update(session);
                    } catch (NumberFormatException e) {
                        Handle handle = session.getHandle();
                        handle.setStep(5);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;
                case 7: {
                    Job job = session.getJob();
                    job.setWorkingHours(messageText);
                    session.setJob(job);
                    sessionService.update(session);
                }
                break;
                case 8: {
                    String phone = getNumber(messageText);
                    if (phone != null) {
                        Job job = session.getJob();
                        job.setContact(messageText);
                        session.setJob(job);
                        sessionService.update(session);
                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(7);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;
                case 9: {
                    Job job = session.getJob();
                    job.setResponsible(messageText);
                    session.setJob(job);
                    sessionService.update(session);
                }
                break;
                case 10: {
                    Job job = session.getJob();
                    job.setAdditional(messageText);
                    session.setJob(job);
                    sessionService.update(session);
                }
                break;
                case 11: {
                    if (messageText.equals("accept")) {
                        Job job = session.getJob();
                        Job createJob = new Job();
                        Handle handle = session.getHandle();
                        handle.setStep(1);
                        handle.setMenuName("main");
                        sessionService.update(session);

                        createJob.setAdditional(job.getAdditional());
                        createJob.setContact(job.getContact());
                        createJob.setDemands(job.getDemands());
                        createJob.setPicUrl(job.getPicUrl());
                        createJob.setPlace(job.getPlace());
                        createJob.setPosition(job.getPosition());
                        createJob.setResponsible(job.getResponsible());
                        createJob.setSalary(job.getSalary());
                        createJob.setUser(session.getUser());
                        createJob.setWorkingHours(job.getWorkingHours());
                        createJob.setStatus("SEND");
                        createJob.setDate(simpleDateFormat.format(new Date()));

                        createJob = jobService.save(createJob);
                        notificationService.notify(new Notification(
                               "ish",createJob),"UserA");
                        sendMessage(chatId, handle.getLang().equals("uz") ? "Ma'lumotlar muvaffaqiyatli joylashtirildi.\nSo'rovingiz admin tomonidan ko'rib chiqilib xabar yuboriladi" : "Данные были успешно размещены");
                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(9);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;

            }
        } else if (session.getHandle().getMenuName().equals("advertisement")) {
            switch (session.getHandle().getStep()) {
                case 2: {
                    if (message.hasPhoto()) {
                        List<PhotoSize> photos = update.getMessage().getPhoto();
                        PhotoSize photo = photos.get(photos.size() - 1);
                        String id = photo.getFileId();
                        try {
                            GetFile getFile = new GetFile();
                            getFile.setFileId(id);
                            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                            String filePath = file.getFilePath();
                            URL url = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath);
                            InputStream in = url.openStream();
                            File file1 = new File("pic/" + file.getFileId() + ".jpg");
                            String path = file1.getCanonicalPath().replace("\\", "/");
                            OutputStream out = new BufferedOutputStream(new FileOutputStream("pic/" + file.getFileId() + ".jpg"));
                            for (int b; (b = in.read()) != -1; ) {
                                out.write(b);
                            }
                            Advertisement advertisement = session.getAdvertisement();
                            advertisement.setPicUrl(file.getFileId() + ".jpg");
                            session.setAdvertisement(advertisement);
                            sessionService.update(session);
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(1);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;
                case 3: {
                    Advertisement advertisement = session.getAdvertisement();
                    advertisement.setText(messageText);
                    session.setAdvertisement(advertisement);
                    sessionService.update(session);
                }
                break;
                case 4: {
                    if (messageText.equals("accept")) {
                        Advertisement createAdvertisement = new Advertisement();
                        Advertisement advertisement = session.getAdvertisement();
                        Handle handle = session.getHandle();
                        handle.setStep(1);
                        handle.setMenuName("main");
                        session.setHandle(handle);
                        sessionService.update(session);

                        createAdvertisement.setPicUrl(advertisement.getPicUrl());
                        createAdvertisement.setText(advertisement.getText());
                        createAdvertisement.setUser(session.getUser());
                        createAdvertisement.setDate(simpleDateFormat.format(new Date()));
                        createAdvertisement.setStatus("SEND");

                        createAdvertisement = advertisementService.save(createAdvertisement);
                        notificationService.notify(new Notification("reklama",createAdvertisement),"UserA");
                        sendMessage(chatId, handle.getLang().equals("uz") ? "Ma'lumotlar muvaffaqiyatli joylashtirildi" : "Данные были успешно размещены");
                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(3);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
            }
        } else if (session.getHandle().getMenuName().equals("resume")) {
            switch (session.getHandle().getStep()) {
                case 2: {
                    if (message.hasPhoto()) {
                        List<PhotoSize> photos = update.getMessage().getPhoto();
                        PhotoSize photo = photos.get(photos.size() - 1);
                        String id = photo.getFileId();
                        try {
                            GetFile getFile = new GetFile();
                            getFile.setFileId(id);
                            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                            String filePath = file.getFilePath();
                            URL url = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath);
                            InputStream in = url.openStream();
                            File file1 = new File("pic/" + file.getFileId() + ".jpg");
                            String path = file1.getCanonicalPath().replace("\\", "/");
                            OutputStream out = new BufferedOutputStream(new FileOutputStream("pic/" + file.getFileId() + ".jpg"));
                            for (int b; (b = in.read()) != -1; ) {
                                out.write(b);
                            }
                            Resume resume = session.getResume();
                            resume.setPicUrl(file.getFileId() + ".jpg");
                            session.setResume(resume);
                            sessionService.update(session);
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(1);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
                break;
                case 3: {
                    Resume resume = session.getResume();
                    resume.setFirstname(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 4: {
                    Resume resume = session.getResume();
                    resume.setLastname(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 5: {
                    Resume resume = session.getResume();
                    resume.setFathersName(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 6: {
                    Resume resume = session.getResume();
                    resume.setDateOfBirth(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 7: {
                    Resume resume = session.getResume();
                    resume.setAddress(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 8: {
                    Resume resume = session.getResume();
                    resume.setMaritalStatus(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 9: {
                    Resume resume = session.getResume();
                    resume.setEmail(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 10: {
                    Resume resume = session.getResume();
                    resume.setPhone(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 11: {
                    Resume resume = session.getResume();
                    resume.setEducation(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 12: {
                    Resume resume = session.getResume();
                    resume.setLanguage(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 13: {
                    Resume resume = session.getResume();
                    resume.setJobExperience(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 14: {
                    Resume resume = session.getResume();
                    resume.setPurpose(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 15: {
                    Resume resume = session.getResume();
                    resume.setPersonalProperty(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 16: {
                    Resume resume = session.getResume();
                    resume.setAdditionalKnowledge(messageText);
                    session.setResume(resume);
                    sessionService.update(session);
                }
                break;
                case 17: {
                    if (messageText.equals("accept")) {
                        Resume createResume = new Resume();
                        Resume resume = session.getResume();
                        Handle handle = session.getHandle();
                        handle.setMenuName("main");
                        handle.setStep(1);
                        session.setHandle(handle);
                        sessionService.update(session);

                        createResume.setFirstname(resume.getFirstname());
                        createResume.setLastname(resume.getLastname());
                        createResume.setFathersName(resume.getFathersName());
                        createResume.setAdditionalKnowledge(resume.getAdditionalKnowledge());
                        createResume.setPersonalProperty(resume.getPersonalProperty());
                        createResume.setPurpose(resume.getPurpose());
                        createResume.setEducation(resume.getEducation());
                        createResume.setJobExperience(resume.getJobExperience());
                        createResume.setLanguage(resume.getLanguage());
                        createResume.setPhone(resume.getPhone());
                        createResume.setPicUrl(resume.getPicUrl());
                        createResume.setDateOfBirth(resume.getDateOfBirth());
                        createResume.setEmail(resume.getEmail());
                        createResume.setMaritalStatus(resume.getMaritalStatus());
                        createResume.setUser(session.getUser());
                        createResume.setAddress(resume.getAddress());
                        createResume.setDate(simpleDateFormat.format(new Date()));
                        createResume.setStatus("SEND");
                        createResume = resumeService.save(createResume);
                        notificationService.notify(new Notification("rezyumi",createResume),"UserA");
                        sendMessage(chatId, handle.getLang().equals("uz") ? "Ma'lumotlar muvaffaqiyatli joylashtirildi" : "Данные были успешно размещены");

                    } else {
                        Handle handle = session.getHandle();
                        handle.setStep(16);
                        session.setHandle(handle);
                        sessionService.update(session);
                    }
                }
            }
        }


        /******* Input parametrs **********/

        /////////////////////////////////////////////////////////////////////////////////////////////////////////

        /******* Output parametrs **********/

        if (session.getHandle().getMenuName().equals("start")) {
            switch (session.getHandle().getStep()) {
                case 1: {
                    sendMessage(chatId, "Assalomu alaykum!\n" +
                            "Rabota v Navoi botiga xush kelibsiz!\n\n" +
                            "Привет!\n" +
                            "Добро пожаловать на бот Работа в Навои");
                    sendMessage(chatId, "Tilni tanlang!/Выберите язык!", session.getHandle(), false, false);
                    Handle handle = session.getHandle();
                    handle.setStep(2);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
            }
        } else if (session.getHandle().getMenuName().equals("main")) {
            if (session.getHandle().getLang().equals("uz")) {
                sendMessage(chatId, "Bosh menyu", session.getHandle(), false, true);
            } else {
                sendMessage(chatId, "Главное меню", session.getHandle(), false, true);
            }
        } else if (session.getHandle().getMenuName().equals("employee")) {
            switch (session.getHandle().getStep()) {
                case 1: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Rasmingizni yuboring:" : "Отправить картинку:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 2: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Ism familiyangizni kiriting:" : "Введите свои имя и фамилию:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 3: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Yoshingizni kiriting:" : "Введите свой возраст:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 4: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Bilim va tajribalaringiz haqida ma'lumot kiriting:" : "Введите информацию о своих знаниях и опыте:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 5: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Rejangizdagi maoshni kiriting:" : "Введите зарплату в свой план:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 6: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Ma'lumotingizni kiriting:" : "Введите свой образования:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 7: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Qo'shimcha ma'lumotlaringizni  kiriting:" : "Введите дополнительную информацию:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 8: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Telefon raqamingizni kiriting:" : "Введите свой номер телефона:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 9: {
                    Boolean isFemale = session.getEmployee().getName().matches("(.*)va (.*)");
                    String pic = isFemale ? "\uD83D\uDC69\u200D\uD83D\uDCBC" : "\uD83D\uDC68\u200D\uD83D\uDCBC";
                    sendImage(chatId, "Ish kerak\n\n" +
                            pic + "*Xodim:* " + session.getEmployee().getName() + "\n" +
                            "\uD83D\uDD51 *Yoshi:* " + session.getEmployee().getAge() + "\n" +
                            "\uD83D\uDCDA *Bilim va tajribalari:* " + session.getEmployee().getExperience() + "\n" +
                            "\uD83D\uDCB0 *Maoshi:* " + session.getEmployee().getSalary() + "\n" +
                            "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB *Ma'lumoti:* " + session.getEmployee().getEducation() + "\n" +
                            "‼️ *Qo'shimcha ma'lumotlar:* " + session.getEmployee().getAdditional() + "\n" +
                            "\uD83D\uDCF1 *Telegram:* [" + session.getUser().getFirstname() + "](tg://user?id=" + session.getUser().getId() + ")\n" +
                            "\uD83D\uDCDE *Telefon:* " + session.getEmployee().getPhone(), session.getEmployee().getPicUrl());
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
            }
        } else if (session.getHandle().getMenuName().equals("job")) {
            switch (session.getHandle().getStep()) {
                case 1: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Rasmingizni yuboring:" : "Отправьте свою фотографию", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 2: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Ish joyini kiriting:" : "Введите рабочее место:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 3: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Lavozimni kriting:" : "Введите должность:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 4: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Talablarni kiriting:" : "Введите требования:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 5: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Maoshni kiriting:" : "Введите зарплату:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 6: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Ish vaqtini kiriting:" : "Введите рабочее время:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 7: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Aloqa vositalarini kiriting:" : "Введите средства связи:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 8: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Mas'ul shaxs ism familiyasini kiriting:" : "Введите имя и фамилию ответственного лица:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 9: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Qo'shimcha ma'lumotlarni kiriting:" : "Введите дополнительную информацию:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 10:{
                    CreatePost.generateJob(session.getJob());
                    sendImage(chatId, "Xodim kerak\n\n" +
                            "\uD83C\uDFE2 *Ish joyi:* " + session.getJob().getPlace() + "\n" +
                            "\uD83D\uDD51 *Lavozim:* " + session.getJob().getPosition() + "\n" +
                            "\uD83D\uDCDA *Talablar:* " + session.getJob().getDemands() + "\n" +
                            "\uD83D\uDCB0 *Maoshi:* " + session.getJob().getSalary() + "\n" +
                            "\uD83D\uDD52 *Ish vaqti:* "+session.getJob().getWorkingHours()+"\n"+
                            "\uD83D\uDCF1 *Telegram:* [" + session.getUser().getFirstname() + "](tg://user?id=" + session.getUser().getId() + ")\n" +
                            "\uD83D\uDCDE *Aloqa:* " + session.getJob().getContact()+"\n" +
                            "‼️ *Qo'shimcha ma'lumotlar:* " + session.getJob().getAdditional()+"\n" +
                            "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB *Mas'ul:* " + session.getJob().getResponsible(), session.getJob().getPicUrl());
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
            }
        } else if (session.getHandle().getMenuName().equals("advertisement")) {
            switch (session.getHandle().getStep()) {
                case 1: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Reklama rasmini yuboring:" : "Отправить рекламное фото:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 2: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Reklama matnini kiriting:" : "Введите текст объявления:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 3:{
                    sendImage(chatId,session.getAdvertisement().getText(),session.getAdvertisement().getPicUrl());
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep()+1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
            }
        } else if (session.getHandle().getMenuName().equals("resume")) {

            switch (session.getHandle().getStep()) {
                case 1: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "3x4 rasmingizni yuboring:" : "Отправить фотографию 3x4:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 2: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Ismingizni kiriting:" : "Введите свое имя:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 3: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Familiyangizni kiriting:" : "Введите свое фамилия:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 4: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Otangizni ismini kiriting:" : "Введите свое отчество:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 5: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Tug'ilgan kunigizni kiriting:" : "Введите свой день рождения:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 6: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Manzilingizni kiriting:" : "Введите свой адрес:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 7: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Oilaviy holatingizni kiriting:" : "Введите свой семейный статус:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 8: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Elektron pochtangizni kiriting:" : "Введите свой адрес электронной почты:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 9: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Telefon raqamingizni kiriting:" : "Введите свой номер телефона:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 10: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Ma'lumotingizni kiriting:" : "Введите свой образования:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 11: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Bilgan tillaringizni kiriting:" : "Введите языки, которые вы знаете:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 12: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Ish tajribalaringizni kiriting:" : "Введите свой опыт работы:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 13: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Maqsadlaringizni kiriting:" : "Введите свои цели:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 14: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Shaxsiy xususiyatlaringizni kiriting:" : "Введите свои личные характеристики:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 15: {
                    sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Qo'shimcha ma'lumotlaringizni kiriting:" : "Введите дополнительную информацию:", session.getHandle(), false, true);
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }
                break;
                case 16:{
                    Boolean isFemale = session.getResume().getLastname().matches("(.*)va (.*)");
                    String pic = isFemale ? "\uD83D\uDC69\u200D\uD83D\uDCBC" : "\uD83D\uDC68\u200D\uD83D\uDCBC";
                    sendImage(chatId, "Ma'lumotlar\n\n" +
                            pic + "*Ism sharifi:* " + session.getResume().getFirstname()+" "+session.getResume().getLastname() +" "+session.getResume().getFathersName()+ "\n" +
                            "\uD83D\uDD51 *Tug'ilgan sanasi:* " + session.getResume().getDateOfBirth() + "\n" +
                            "\uD83D\uDCDA *Manzil:* " + session.getResume().getAddress() + "\n" +
                            "\uD83D\uDCB0 *Telefon:* " + session.getResume().getPhone() + "\n" +
                            "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB *Ma'lumoti:* " + session.getResume().getEducation() + "\n" +
                            "‼️ *Qaysi xorijiy tillarni biladi:* " + session.getResume().getLanguage() + "\n" +
                            "\uD83D\uDCF1 *Oilaviy holati:* " + session.getResume().getMaritalStatus() + "\n" +
                            "\uD83D\uDCDE *Kasbiy faoliyati:* " + session.getResume().getJobExperience()+"\n" +
                            "*Maqsadi:* "+session.getResume().getPurpose()+"\n" +
                            "*Shaxsiy xususiyatlari:* "+session.getResume().getPersonalProperty()+"\n" +
                            "*Qo'shimcha bilimlari:* "+session.getResume().getAdditionalKnowledge(), session.getResume().getPicUrl());
                    Handle handle = session.getHandle();
                    handle.setStep(handle.getStep() + 1);
                    session.setHandle(handle);
                    sessionService.update(session);
                }

            }

        } else if (session.getHandle().getMenuName().equals("setting")) {
            sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Kerakli bo'limni tanlang \uD83D\uDC47" : "Выберите нужный раздел \uD83D\uDC47", session.getHandle(), false, true);
        } else if (session.getHandle().getMenuName().equals("lang")) {
            sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Muloqot uchun tilni tanlang" : "Выберите язык для общения", session.getHandle(), false, false);
        } else if (session.getHandle().getMenuName().equals("info")) {
            sendMessage(chatId, session.getHandle().getLang().equals("uz") ? "Biz haqimizda" : "О нас");
            Handle handle = session.getHandle();
            handle.setMenuName("main");
            session.setHandle(handle);
        }


        /******* Output parametrs **********/

        //////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    @Override
    public String getBotUsername() {
        return "russianlessonBot";
    }

    public void setReplyKeyboard(SendMessage sendMessage, Handle handle) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row, row1, row2;
        if (handle.getLang().equals("uz")) {
            if (handle.getMenuName().equals("main") && handle.getStep() == 1) {
                row = new KeyboardRow();
                row1 = new KeyboardRow();
                row2 = new KeyboardRow();
                row.add(new KeyboardButton("\uD83C\uDFE2 Ish kerak"));
                row.add(new KeyboardButton("\uD83D\uDC68\u200D\uD83D\uDCBB Xodim kerak"));
                row1.add(new KeyboardButton("\uD83D\uDDBC Reklama berish"));
                row1.add(new KeyboardButton("\uD83D\uDCD1 Rezyumi shakllantirish"));
                row2.add(new KeyboardButton("ℹ️ Biz haqimizda"));
                row2.add(new KeyboardButton("⚙️Sozlash"));
                rows.add(row);
                rows.add(row1);
                rows.add(row2);
            } else if (handle.getMenuName().equals("employee")) {
                switch (handle.getStep()) {
                    case 1: {
                        row = new KeyboardRow();
                        row.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                    }
                    break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Orqaga"));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                        rows.add(row1);
                    }
                    break;
                    case 8: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Orqaga"));
                        row.add(new KeyboardButton("\uD83D\uDCF1 Telefon raqamni yuborish").setRequestContact(true));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                        rows.add(row1);
                    }
                }
            } else if (handle.getMenuName().equals("job")) {
                switch (handle.getStep()) {
                    case 1: {
                        row = new KeyboardRow();
                        row.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                    }
                    break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 8:
                    case 9: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Orqaga"));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                        rows.add(row1);
                    }
                    break;
                    case 7: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Orqaga"));
                        row.add(new KeyboardButton("\uD83D\uDCF1 Telefon raqamni yuborish").setRequestContact(true));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                        rows.add(row1);

                    }
                    break;
                }
            } else if (handle.getMenuName().equals("advertisement")) {
                switch (handle.getStep()) {
                    case 1: {
                        row = new KeyboardRow();
                        row.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                    }
                    break;
                    case 2: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Orqaga"));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                        rows.add(row1);
                    }
                    break;
                }
            } else if (handle.getMenuName().equals("setting")) {
                row = new KeyboardRow();
                row1 = new KeyboardRow();
                row.add(new KeyboardButton("\uD83C\uDF0E Tilni o'zgartirish"));
                row1.add(new KeyboardButton("⬅️ Bosh menyuga qaytish"));
                rows.add(row);
                rows.add(row1);
            } else if (handle.getMenuName().equals("resume")) {
                switch (handle.getStep()) {
                    case 1: {
                        row = new KeyboardRow();
                        row.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                    }
                    break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Orqaga"));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                        rows.add(row1);
                    }
                    break;
                    case 9: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Orqaga"));
                        row.add(new KeyboardButton("\uD83D\uDCF1 Telefon raqamni yuborish").setRequestContact(true));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Bekor qilish"));
                        rows.add(row);
                        rows.add(row1);
                    }
                }
            }
        } else {
            if (handle.getMenuName().equals("main") && handle.getStep() == 1) {
                row = new KeyboardRow();
                row1 = new KeyboardRow();
                row2 = new KeyboardRow();
                row.add(new KeyboardButton("\uD83C\uDFE2 Нужна работа"));
                row.add(new KeyboardButton("\uD83D\uDC68\u200D\uD83D\uDCBB Нужен сотрудник"));
                row1.add(new KeyboardButton("\uD83D\uDDBC Реклама"));
                row1.add(new KeyboardButton("\uD83D\uDCD1 Написать резюме"));
                row2.add(new KeyboardButton("ℹ️ О нас"));
                row2.add(new KeyboardButton("⚙️Настройки"));
                rows.add(row);
                rows.add(row1);
                rows.add(row2);
            } else if (handle.getMenuName().equals("employee")) {
                switch (handle.getStep()) {
                    case 1: {
                        row = new KeyboardRow();
                        row.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                    }
                    break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Назад"));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                        rows.add(row1);
                    }
                    break;
                    case 8: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Назад"));
                        row.add(new KeyboardButton("\uD83D\uDCF1 Отправить номер телефона").setRequestContact(true));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                        rows.add(row1);
                    }
                }
            } else if (handle.getMenuName().equals("job")) {
                switch (handle.getStep()) {
                    case 1: {
                        row = new KeyboardRow();
                        row.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                    }
                    break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 8:
                    case 9: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Назад"));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                        rows.add(row1);
                    }
                    break;
                    case 7: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Назад"));
                        row.add(new KeyboardButton("\uD83D\uDCF1 Отправить номер телефона").setRequestContact(true));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                        rows.add(row1);

                    }
                    break;
                }
            } else if (handle.getMenuName().equals("advertisement")) {
                switch (handle.getStep()) {
                    case 1: {
                        row = new KeyboardRow();
                        row.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                    }
                    break;
                    case 2: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Назад"));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                        rows.add(row1);
                    }
                    break;
                }
            } else if (handle.getMenuName().equals("setting")) {
                row = new KeyboardRow();
                row1 = new KeyboardRow();
                row.add(new KeyboardButton("\uD83C\uDF0E Изменить язык"));
                row1.add(new KeyboardButton("⬅️ На главную"));
                rows.add(row);
                rows.add(row1);
            } else if (handle.getMenuName().equals("resume")) {
                switch (handle.getStep()) {
                    case 1: {
                        row = new KeyboardRow();
                        row.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                    }
                    break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Назад"));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                        rows.add(row1);
                    }
                    break;
                    case 9: {
                        row = new KeyboardRow();
                        row1 = new KeyboardRow();
                        row.add(new KeyboardButton("⬅️ Назад"));
                        row.add(new KeyboardButton("\uD83D\uDCF1 Отправить номер телефона").setRequestContact(true));
                        row1.add(new KeyboardButton("\uD83D\uDEAB Отмена"));
                        rows.add(row);
                        rows.add(row1);
                    }
                }
            }
        }
        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

    }

    public void setInlineKeyboard(SendMessage sendMessage, Handle handle) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        if ((handle.getMenuName().equals("start") && handle.getStep() == 1) || handle.getMenuName().equals("lang")) {
            buttons.add(new InlineKeyboardButton().setCallbackData("uz").setText("\uD83C\uDDFA\uD83C\uDDFF O'zbekcha"));
            buttons1.add(new InlineKeyboardButton().setCallbackData("ru").setText("\uD83C\uDDF7\uD83C\uDDFA Русский"));
            list.add(buttons);
            list.add(buttons1);
        }
        inlineKeyboardMarkup.setKeyboard(list);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    public String getNumber(String string) {
        Pattern pattern = Pattern.compile("(.*)99\\d{7}(.*)|(.*)90\\d{7}(.*)|(.*)91\\d{7}(.*)" +
                "|(.*)93\\d{7}(.*)|(.*)94\\d{7}(.*)|(.*)95\\d{7}(.*)|(.*)97\\d{7}(.*)" +
                "|(.*)98\\d{7}(.*)");
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    public void sendMessage(Long chatId, String text, Handle handle, Boolean enableMarkdown, Boolean isReply) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableMarkdown(enableMarkdown);
        if (isReply) {
            setReplyKeyboard(sendMessage, handle);
        } else {
            setInlineKeyboard(sendMessage, handle);
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendImage(Long chatId, String text) {
        File file = new File("pic/AgACAgIAAxkBAAIK7l9wXXRYPCdQQAPWQtY67d1tMxybAAI8sDEbqqWJSzq7ZL-ilcK3Q4vali4AAwEAAwIAA3kAA8CqAQABGwQ.jpg");
        String path = "";
        try {
            path = file.getCanonicalPath().replace("\\", "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption("Rasm tanlash");
        sendPhoto.setPhoto(new File(path));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(new InlineKeyboardButton().setCallbackData("previous").setText("⏪"));
        row1.add(new InlineKeyboardButton().setCallbackData("1").setText("1/1"));
        row1.add(new InlineKeyboardButton().setCallbackData("next").setText("⏩"));
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(new InlineKeyboardButton().setCallbackData("img").setText("Tanlash"));
        keyboard.add(row1);
        keyboard.add(row2);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void sendImage(Long chatId, String text, String imgUrl) {
        File file = new File("pic/" + imgUrl);
        String path = "";
        try {
            path = file.getCanonicalPath().replace("\\", "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption(text);
        sendPhoto.setPhoto(new File(path));
        sendPhoto.setParseMode("Markdown");
        sendPhoto.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(new InlineKeyboardButton().setCallbackData("accept").setText("✅ Tasdiqlash"));
        row2.add(new InlineKeyboardButton().setCallbackData("\uD83D\uDEAB Bekor qilish").setText("\uD83D\uDEAB Bekor qilish"));
        keyboard.add(row1);
        keyboard.add(row2);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendImage(Long chatId, String text, String imgUrl, String type){
        File file = new File("pic/" + imgUrl);
        String path = "";
        try {
            path = file.getCanonicalPath().replace("\\", "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption(text);
        sendPhoto.setPhoto(new File(path));
        sendPhoto.setParseMode("Markdown");
        sendPhoto.setChatId(chatId);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
