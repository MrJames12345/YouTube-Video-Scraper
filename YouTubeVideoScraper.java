import java.util.*;
import java.io.*;
 // Webscraping
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


// Class only has one public method and imported variable only used within this method, so called statically from other classes
class YouTubeVideoScraper
{


// - Get Song Tags Method - //


    // Get a given video's title, artist and album
    public static HashMap<String, String> getSongTags( String inVideoUrl ) throws IOException, NotYouTubeSongException
    {

        // Beginning of all tags
        String textAtTagsLeft = "\"},\"description\":{\"simpleText\":\"";
        int textAtTagsLeftLen = 32;
        // Title, Artist, Album
        String providedToYtCheck = "Provided to YouTube by";
        String textAtTitleLeftOption1 = "\\n\\n";
        int textAtTitleLeftLen = 4;
        String textBetweenTitleArtist = " " + (char)183 + " ";
        int textBetweenTitleArtistLen = 3;
        String textBetweenArtistAlbum = "\\n\\n";
        int textBetweenArtistAlbumLen = 4;
        String textAtAlbumRight = "\\n\\n";

        int tagsLeft;
        int titleLeft;
        int titleRight;
        int artistLeft;
        int artistRight;
        int albumLeft;
        int albumRight;

        String title = null;
        String artist = null;
        String album = null;

        HashMap<String, String> outTags = new HashMap<String, String>();
        String videoHtml = null;
        
        // Get page's html
        videoHtml = Jsoup.connect( inVideoUrl ).get().html();

        // IF html does not include required text, error in retrieving html originally, so throw exception
        if ( videoHtml.indexOf( textBetweenTitleArtist ) < 0 )
        {
            throw new NotYouTubeSongException();
        }

        // Get tags position
        tagsLeft = videoHtml.indexOf( textAtTagsLeft ) + textAtTagsLeftLen;

        // Get title position
        // IF html includes "Provided to YouTube by...", title starting position is at "\\\\n\\\\n"
        if ( videoHtml.indexOf( providedToYtCheck, tagsLeft ) > 0 )
        {
            titleLeft = videoHtml.indexOf( textAtTitleLeftOption1, tagsLeft ) + textAtTitleLeftLen;
        }
        // ELSE set titleLeft as just tagsLeft
        else
        {
            titleLeft = tagsLeft;
        }

        titleRight = videoHtml.indexOf( textBetweenTitleArtist, titleLeft );

        // Get artist and album positions
        artistLeft = titleRight + textBetweenTitleArtistLen;
        artistRight = videoHtml.indexOf( textBetweenArtistAlbum, artistLeft );
        albumLeft = artistRight + textBetweenArtistAlbumLen;
        albumRight = videoHtml.indexOf( textAtAlbumRight, albumLeft );

        // Get Title, artist and album
        title = replaceTagChars( videoHtml.substring( titleLeft, titleRight ) );
        artist = replaceTagChars( videoHtml.substring( artistLeft, artistRight ) );
        album = replaceTagChars( videoHtml.substring( albumLeft, albumRight ) );

        // Add to returning map and return
        outTags.put( "title", title );
        outTags.put( "artist", artist );
        outTags.put( "album", album );

        return outTags;

    }


    // Replace chars in tag text
    public static String replaceTagChars( String inTag )
    {
        return inTag.replace( (char)183, '&' ).replace( "\\\\u0026", "&" );
    }

}