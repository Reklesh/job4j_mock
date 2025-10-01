package ru.checkdev.site.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.site.dto.FeedbackDTO;
import ru.checkdev.site.dto.InterviewDTO;
import ru.checkdev.site.service.FeedbackService;
import ru.checkdev.site.service.InterviewService;

import javax.servlet.http.HttpServletRequest;

/**
 * FeedbackController контроллер обработки отзывов
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 25.10.2023
 */
@Controller
@RequestMapping("/interview")
@AllArgsConstructor
@Slf4j
public class FeedbackController {

    private FeedbackService feedbackService;

    private InterviewService interviewService;

    /**
     * Отображение формы для сохранения отзыв на собеседование.
     *
     * @param id int ID Interview
     * @return String FeedbackForm page
     */
    @GetMapping("/feedback/{id}")
    public String getFeedbackForm(@PathVariable int id,
                                  Model model,
                                  HttpServletRequest request) {
        var token = RequestResponseTools.getToken(request);
        var interviewDTO = new InterviewDTO();
        try {
            interviewDTO = interviewService.getById(token, id);
        } catch (Exception e) {
            log.error("InterviewService.class method getById error: {}", e.getMessage());
            return "redirect:/";
        }
        model.addAttribute("interview", interviewDTO);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Собеседования", "/interviews/",
                interviewDTO.getTitle(), String.format("/interview/%d", id),
                "Отзыв", String.format("/interview/feedback/%d", id));
        return "/interview/feedbackForm";
    }

    /**
     * Метод пост сохраняет новый комментарий.
     *
     * @param feedbackDTO FeedbackDTO
     * @param request     HttpServletRequest
     * @return String page.
     */
    @PostMapping("/createFeedback")
    public String saveFeedback(@ModelAttribute FeedbackDTO feedbackDTO,
                               HttpServletRequest request) {
        var token = RequestResponseTools.getToken(request);
        feedbackService.save(token, feedbackDTO);
        return "redirect:/interview/" + feedbackDTO.getInterviewId();
    }
}
