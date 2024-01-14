package fr.emmathie;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@CrossOrigin
@RequestMapping(path = "/api")
public class MainController implements InitializingBean {

	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private ConfigRepository configRepository;

	@PostMapping(path = "/new")
	public @ResponseBody ResponseEntity<String> addTask(@RequestParam String type, @RequestBody String body) {
		ObjectMapper mapper = new ObjectMapper();
		AbstractTask at;
		try {
			switch (type) {
			case "TASK":
				Task t = mapper.readValue(body, Task.class);
				at = t;
				break;
			case "HABIT":
				Habit h = mapper.readValue(body, Habit.class);
				at = h;
				break;

			default:
				return new ResponseEntity<String>("Type must be HABIT or TASK", HttpStatus.BAD_REQUEST);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Json Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		at = taskRepository.save(at);
		String json;
		try {
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(at);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<String>("Json Error in return: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

	@PutMapping(path = "/edit")
	public @ResponseBody ResponseEntity<String> editTask(@RequestParam int id, @RequestBody String body) {
		ObjectMapper mapper = new ObjectMapper();
		AbstractTask at = taskRepository.findById(id).orElse(null);
		if (at == null)
			return new ResponseEntity<String>("Task id " + id + " not found", HttpStatus.BAD_REQUEST);
		AbstractTask other;
		try {
			if (at instanceof Task) {
				other = mapper.readValue(body, Task.class);
			} else if (at instanceof Habit) {
				other = mapper.readValue(body, Habit.class);
			} else {
				return new ResponseEntity<String>("Neither an habit or a task ???", HttpStatus.BAD_REQUEST);
			}
			System.out.println("Other: " + other);
			System.out.println(body);
			at.merge(other);
			String json;
			try {
				json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(at);
			} catch (JsonProcessingException e) {
				return new ResponseEntity<String>("Json Error in return: " + e.getMessage(), HttpStatus.BAD_REQUEST);
			}
			taskRepository.save(at);
			return new ResponseEntity<String>(json, HttpStatus.OK);
		} catch (IOException | MergeException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Json Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(path = "/check")
	public @ResponseBody ResponseEntity<String> validate(@RequestParam int id, @RequestParam boolean valid) {
		AbstractTask at = taskRepository.findById(id).orElse(null);
		if (at == null)
			return new ResponseEntity<String>("Task " + id + " not found", HttpStatus.BAD_REQUEST);
		if (at.isValidated() == valid)
			return new ResponseEntity<String>("Task " + id + " validated already = " + valid, HttpStatus.OK);
		at.setValidated(valid);
		taskRepository.save(at);
		return new ResponseEntity<String>("Task " + id + " validated = " + valid, HttpStatus.OK);
	}

	@GetMapping(path = "/all")
	public @ResponseBody Iterable<AbstractTask> getAll() {
		// This returns a JSON or XML with the users
		return taskRepository.findAll();
	}

	@GetMapping(path = "/habits")
	public @ResponseBody Iterable<Habit> getHabits() {
		// This returns a JSON or XML with the users
		ArrayList<Habit> ret = new ArrayList<Habit>();
		Iterator<AbstractTask> i = taskRepository.findAll().iterator();
		while (i.hasNext()) {
			AbstractTask at = i.next();
			if (at instanceof Habit)
				ret.add((Habit) at);
		}
		return ret;
	}

	@GetMapping(path = "/tasks")
	public @ResponseBody Iterable<Task> getTasks() {
		// This returns a JSON or XML with the users
		ArrayList<Task> ret = new ArrayList<Task>();
		Iterator<AbstractTask> i = taskRepository.findAll().iterator();
		while (i.hasNext()) {
			AbstractTask at = i.next();
			if (at instanceof Task)
				ret.add((Task) at);
		}
		return ret;
	}

	@GetMapping(path = "/get")
	public @ResponseBody ResponseEntity<String> getTasks(@RequestParam int id) {
		AbstractTask at = taskRepository.findById(id).orElse(null);
		if (at == null)
			return new ResponseEntity<String>("Item doesn't exist " + id, HttpStatus.BAD_REQUEST);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(at);
			return new ResponseEntity<String>(json, HttpStatus.OK);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<String>("Json Error in return: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping(path = "/delete")
	public @ResponseBody ResponseEntity<String> deleteTask(@RequestParam int id) {
		AbstractTask at = taskRepository.findById(id).orElse(null);
		if (at == null)
			return new ResponseEntity<String>("Task " + id + " not found", HttpStatus.BAD_REQUEST);
		at.clean();
		taskRepository.deleteById(id);
		return new ResponseEntity<String>("deleted task " + id, HttpStatus.OK);
	}

	@GetMapping(path = "/config")
	public @ResponseBody Iterable<Config> getConfig() {
		return configRepository.findAll();
	}

	@PutMapping(path = "/config")
	public @ResponseBody ResponseEntity<String> setConfig(@RequestParam String key, @RequestParam String value) {
		Config c = configRepository.findById(key).orElse(new Config(key, null));
		String previous = c.getValue();
		c.setValue(value);
		configRepository.save(c);
		return new ResponseEntity<String>("Set " + key + " to " + value + ", was " + previous, HttpStatus.OK);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Initializing default config
		for (DConfig dc : DConfig.values()) {
			if (!configRepository.existsById(dc.key)) {
				Config c = new Config(dc.key, dc.value);
				configRepository.save(c);
			}
		}
		startExecutors();
	}

	private void startExecutors() {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		// TODO use a config value for daily reset time
		Long execTime = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay().withHour(6),
				ChronoUnit.MINUTES);
		executor.scheduleAtFixedRate(new DailyTask(), execTime, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
	}

	private class DailyTask extends TimerTask {
		@Override
		public void run() {
			for (AbstractTask at : taskRepository.findAll()) {
				if (at instanceof Habit) {
					((Habit) at).dailyReset();
				}
			}
		}
	}
}
