package net.starkenberg.logging.elastic.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.starkenberg.logging.elastic.schedule.ScheduledMethods;

/**
 * @author Brad Starkenberg
 *
 */
@RestController
@RequestMapping("/api")
public class MainController {
	@Autowired
	private ScheduledMethods service;
	
	@RequestMapping(method=RequestMethod.DELETE, path="/indices")
	public ResponseEntity<String> deleteOldIndices() {
		service.deleteIndices();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.DELETE, path="/indices/{index}")
	public ResponseEntity<String> deleteAnIndex(@PathVariable String index) {
		service.deleteAnIndex(index);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path="/indices")
	public ResponseEntity<List<String>> getIndicesThatNeedDeleted() {
		return new ResponseEntity<List<String>>(service.getIndicesToDelete(), HttpStatus.OK);
	}
}
