package com.okayjava.html.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.okayjava.html.model.User;

@Controller
public class IndexController {

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	@PostMapping("/register")
	public ModelAndView action(@RequestParam(value = "ingredient") List<String> paramValues, @ModelAttribute User user,
			Model model) throws InterruptedException {

		System.out.println(user.toString());
		System.out.println(user.getIngredients());
		model.addAttribute(paramValues);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("output");

		modelAndView.addObject("ingredient", user.getIngredients());

		modelAndView.addObject("ingredients", paramValues);
		List<String> copyValues = new ArrayList<String>();
		copyValues = user.getCopyValues();
		copyValues.addAll(paramValues);
		System.out.println("copyValues" + copyValues);
		user.setCopyValues(copyValues);

		TimeUnit.SECONDS.sleep(30);
		return modelAndView;

	}

	/*
	 * 
	 * @PostMapping("/register") public String userSubmit(@ModelAttribute User user,
	 * Model model) { System.out.println(user.toString());
	 * System.out.println(user.getIngredients());
	 * 
	 * model.addAttribute("ingredient", user.getIngredients());
	 * 
	 * return "output";
	 * 
	 * }
	 */

}
