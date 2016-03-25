/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.animbus.music.media;

/**
 * Just here to store some code I may use later
 */
public class LibraryTempCode {

    /*public static void buildSongsForAlbum(Album album) {
        new AsyncTask<Object, Void, List<Song>>() {
            @Override
            protected List<Song> doInBackground(Object... params) {
                List<Song> generated = new ArrayList<>();
                try {
                    Cursor albumSongsCursor = ((Context) params[0]).getContentResolver().query(
                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            new String[]{}, MediaStore.Audio.Media.ALBUM_ID + "?=",
                            new String[]{String.valueOf((long) params[1])},
                            MediaStore.Audio.Media.TRACK);

                    assert albumSongsCursor != null : "Cursor is null";
                    int idColumn = albumSongsCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int trackNumber = albumSongsCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

                    albumSongsCursor.moveToFirst();
                    do {
                        Song s = new Song();


                        s.setId(albumSongsCursor.getLong(idColumn));
                        s.setTrackNumber(albumSongsCursor.getLong(trackNumber));

                        generated.add(s);
                    } while (albumSongsCursor.moveToNext());
                    albumSongsCursor.close();
                } catch (IndexOutOfBoundsException e) {
                    generated = Collections.emptyList();
                }
                return generated;
            }

            @Override
            protected void onPostExecute(List<Song> songs) {
                super.onPostExecute(songs);
                album.setSongs(songs);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, album.getId(), album);
    }

    public static void injectSongIntoAlbum(Song... values) {
        if (mSongsBuilt) for (Song song : values) {
            for (Album a : getAlbums()) ;
        }
    }

    public static void buildSongsForPlaylist(Playlist... values) {
        for (Playlist playlist : values)
            new AsyncTask<Object, Void, Void>() {
                @Override
                protected Void doInBackground(Object... params) {
                    List<Song> generated = new ArrayList<>();
                    try {
                        Cursor playlistSongsCursor = ((Context) params[0]).getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri("external", (long) params[1]),
                                null, null, null, MediaStore.Audio.Playlists.Members.PLAY_ORDER);

                        assert playlistSongsCursor != null : "Cursor is null";
                        int idColumn = playlistSongsCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);

                        playlistSongsCursor.moveToFirst();
                        do {
                            generated.add(Library.findSongById(playlistSongsCursor.getLong(idColumn)));
                        } while (playlistSongsCursor.moveToNext());
                        playlistSongsCursor.close();
                    } catch (IndexOutOfBoundsException e) {
                        generated = Collections.emptyList();
                    }
                    return generated;
                }

                @Override
                protected void onPostExecute(List<Song> songs) {
                    super.onPostExecute(songs);
                    Library.findPlaylistById((long) params[1])
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, playlist.getId());
    }*/

}
