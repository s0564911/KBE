package de.htwb.ai.kbe.controller;

import java.util.List;
import java.util.Set;

import de.htwb.ai.kbe.service.ISongListService;
import de.htwb.ai.kbe.service.IUserService;

//import org.graalvm.compiler.lir.LIRInstruction.Use;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import de.htwb.ai.kbe.model.SongList;
import de.htwb.ai.kbe.model.User;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(value = "/songLists")
public class SongListController {
	//frage: soll jeder public songslists bekommen oder nur angemeldete users?
	//Ausgabeformate für GET sind JSON und XML?? was da zu tun
	//die angelegte liste wegen songlists?
	//post checkt noch nicht ob die songs vorhanden sind(gute und schlechte songs aufgabenstellung)
	//validate alle song anfragen, bisher nur getall zum testen
	//songlist set song rename into songlist irgendwo
	//https://forum.hibernate.org/viewtopic.php?p=2480052 addsong als save statt persist worked
	//isprivate funktioniert nicht
    private final ISongListService songListService;
    private final IUserService userService;

    public SongListController(ISongListService songListService,IUserService userService) {
        this.userService = userService;
		this.songListService = songListService;    
    }
    
    /**
     * 
     * @param userId
     * @param optionalHeader
     * @return
     * returns public or private songlist based on if the requester owns the songlist
     */
    @RequestMapping(params="userId",method = RequestMethod.GET)
    public ResponseEntity<List<SongList>> getAllSongs(
    		@RequestParam("userId") String userId,
    		@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {
    	
    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
    	User user =userService.getUserByUserId(userId);
    	if (user != null) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);} ///idk if this comparison works
    	if (userService.compareTokenToUser(optionalHeader, user)) {
    		List<SongList> sl=songListService.getAllSonglistsByUser(user);
    		return new ResponseEntity<>(sl, HttpStatus.OK);
    	}
    	else {
    		List<SongList> sl=songListService.getPublicSonglistsByUser(user);
    		return new ResponseEntity<>(sl, HttpStatus.OK);
    	}	
    }
    /**
     * 
     * @param id
     * @param optionalHeader
     * @return
     * returns songlist based on id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public ResponseEntity<SongList> getSong(@PathVariable("id") int id,
    		@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {
    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
    	SongList songList = songListService.getSonglistById(id);
    	//check if songlist is empty here?
    	if (songListService.getSonglistOwnerById(id).equals(
    			userService.getUserByUserId(userService.getUsernameFromToken(optionalHeader)))){
    				return new ResponseEntity<>(songList, HttpStatus.OK);
    			}
    	else {
    		if (songList.getIspriv()) {
    			return new ResponseEntity<>(HttpStatus.FORBIDDEN); }
    		return new ResponseEntity<>(songList, HttpStatus.OK);	
    	}
    }
    //post
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<SongList> addSongList(@RequestBody SongList s,
    		@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {
    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
    	HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/rest/songs/");
        SongList songList = SongList.builder()  
        		.withName(s.getName())
        		.withIsPriv(s.getIspriv())
        		.withOwnerid(userService.getUserByUserId(userService.getUsernameFromToken(optionalHeader)))
        		.withSongs(s.getSongSet()).build();
        try {
            songListService.addSonglist(songList);
            System.out.println("i added a songlist"+songList);
            
        } catch (Exception e) {
        	System.out.println(e);
            return new ResponseEntity<>(songList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(songList, headers, HttpStatus.CREATED);
    }   
    	
    	

    
    /**
     * 
     * @param id
     * @param optionalHeader
     * @return
     * deletes entry based on id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<SongList> deleteSongList(@PathVariable("id") int id,
    		@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {
    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
    	if (userService.compareTokenToUser(optionalHeader, songListService.getSonglistOwnerById(id))) {
    		  try {
    	            songListService.deleteSonglistById(id);
    	        } catch (EntityNotFoundException e) {
    	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	        }
    	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    
    
    //helper
    public boolean auth (String optionalHeader) {
    	if (optionalHeader.equals("")) {
    		return false;
    	}
    	else if (userService.validateJWT(optionalHeader)) {
    		return true;
    	}
    	else {return false;}
    }

   
}
