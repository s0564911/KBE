package de.htwb.ai.kbe.controller;

import java.util.List;
import java.util.Set;

import de.htwb.ai.kbe.service.ISongListService;
import de.htwb.ai.kbe.service.ISongService;
import de.htwb.ai.kbe.service.IUserService;

//import org.graalvm.compiler.lir.LIRInstruction.Use;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import de.htwb.ai.kbe.model.Song;
import de.htwb.ai.kbe.model.SongList;
import de.htwb.ai.kbe.model.User;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(value = "/songLists")
public class SongListController {
	//Ausgabeformate für GET sind JSON und XML?? was da zu tun
	//die angelegte liste wegen songlists?
	//token secret noch seperat packen
	//post checkt noch nicht ob die songs vorhanden sind(gute und schlechte songs aufgabenstellung)
	//song endpoint kurz testen wegen auth header validierung
	//muss noch testen ob nur public methods geschickt werden bei get?userid=.. wenn noetig
    private final ISongListService songListService;
    private final IUserService userService;
    private final ISongService songService;

    public SongListController(ISongListService songListService,IUserService userService, ISongService songService) {
        this.userService = userService;
		this.songListService = songListService;  
		this.songService = songService;
    }
    
    /**
     * 
     * @param userId
     * @param optionalHeader
     * @return
     * returns public or private songlist based on if the requester owns the songlist
     */
    @RequestMapping(params="userId",method = RequestMethod.GET)
    public ResponseEntity<List<SongList>> getSongByParam(
    		@RequestParam("userId") String userId,
    		@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {
    	
    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
    	User user =userService.getUserByUserId(userId);
    	if (user == null) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);} ///idk if this comparison works
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
    	if (songListService.getSonglistOwnerById(id).getUserId().equals(
    			userService.getUserByUserId(userService.getUsernameFromToken(optionalHeader)).getUserId())){
    				return new ResponseEntity<>(songList, HttpStatus.OK);
    			}
    	else {
    		if (songList.getIsPrivate()) {
    			return new ResponseEntity<>(HttpStatus.FORBIDDEN); }
    		System.out.println(songListService.getSonglistOwnerById(id));
    		System.out.println(userService.getUserByUserId(userService.getUsernameFromToken(optionalHeader)));
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
        
        Set<Song> checkList=s.getSongList();
        for (Song song :checkList) {
        	Song compare=songService.getSongById(song.getId());
        	if (!(compare.getArtist().equals(song.getArtist())&&
        			compare.getLabel().equals(song.getLabel())&&
        			compare.getReleased()==song.getReleased()&&
        			compare.getTitle().equals(song.getTitle()))) {
        		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        	}
        }
        
        
        SongList songList = SongList.builder()  
        		.withName(s.getName())
        		.withIsPriv(s.getIsPrivate())
        		.withOwnerid(userService.getUserByUserId(userService.getUsernameFromToken(optionalHeader)))
        		.withSongs(s.getSongList()).build();
        try {
            songListService.addSonglist(songList); 
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
