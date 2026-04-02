import { Search, XCircleIcon, Loader2, Music, User, Disc } from "lucide-react";
import { useState, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useDebounce } from "../../hooks/useDebounce";
import { searchService } from "../../services/searchService";
import type { SearchResponse } from "../../types/search";

const SearchBar = () => {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const [searchInput, setSearchInput] = useState("");
    const [isFocused, setIsFocused] = useState(false);
    const [results, setResults] = useState<SearchResponse | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const wrapperRef = useRef<HTMLDivElement>(null);

    const debouncedSearch = useDebounce(searchInput, 300);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
                setIsFocused(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    useEffect(() => {
        if (!debouncedSearch.trim() || debouncedSearch.length < 2) {
            setResults(null);
            return;
        }

        const fetchResults = async () => {
            setIsLoading(true);
            try {
                const data = await searchService.search(debouncedSearch, [], 0, 3);
                setResults(data);
            } catch (error) {
                console.error("Search failed:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchResults();
    }, [debouncedSearch]);

    const handleSearchSubmit = () => {
        if (searchInput.trim().length >= 2) {
            setIsFocused(false);
            navigate(`/search?q=${encodeURIComponent(searchInput.trim())}`);
        }
    };

    const hasResults = results && (
        results.tracks.data.length > 0 ||
        results.albums.data.length > 0 ||
        results.artists.data.length > 0
    );

    return (
        <div className="relative flex flex-1 max-w-2xl" ref={wrapperRef}>
            <div className="relative flex flex-1 items-center text-white focus-within:text-gray-200 bg-gray-900/80 backdrop-blur-sm rounded-full z-20">
                <div className="pointer-events-none absolute inset-y-0 left-4 flex items-center">
                    <Search className="h-5 w-5" />
                </div>
                <input
                    className="block w-full rounded-full border border-gray-600 py-2 pl-10 pr-10 text-white placeholder-gray-300 hover:border-gray-500 focus:border-blue-500 focus:bg-gray-800 focus:placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-blue-500 transition-colors"
                    placeholder={t("components.layout.search_bar.search_place_holder")}
                    type="search"
                    value={searchInput}
                    onFocus={() => setIsFocused(true)}
                    onChange={(e) => setSearchInput(e.target.value)}
                    onKeyUp={(e) => {
                        if (e.key === 'Enter') {
                            e.preventDefault();
                            (e.target as HTMLInputElement).blur();
                            handleSearchSubmit();
                        }
                    }}
                />
                {searchInput.length > 0 && (
                    <button
                        className="absolute inset-y-0 right-2 m-auto h-7 w-7 border-none p-1 text-gray-400 outline-none transition hover:text-white focus:border-none focus:outline-none"
                        onClick={() => {
                            setSearchInput("");
                            setResults(null);
                        }}
                    >
                        <XCircleIcon className="h-5 w-5" />
                    </button>
                )}
            </div>

            {/* Dropdown Popover */}
            {isFocused && searchInput.length >= 2 && (
                <div className="absolute top-12 left-0 right-0 bg-gray-800 border border-gray-700 rounded-xl shadow-2xl overflow-hidden z-20">
                    {isLoading ? (
                        <div className="flex justify-center items-center p-6 text-gray-400">
                            <Loader2 className="h-6 w-6 animate-spin mr-2" />
                            <span>{t('components.layout.search_bar.searching')}</span>
                        </div>
                    ) : (hasResults ? (
                        <div className="max-h-[70vh] overflow-y-auto font-semibold">
                            {/* Tracks */}
                            {results.tracks.data.length > 0 && (
                                <div className="p-2">
                                    <h3 className="px-3 py-2 flex gap-2 text-xs text-gray-400 uppercase tracking-wider items-center">
                                        <Music className="h-4 w-4" /> {t('search.tabs.tracks')}
                                    </h3>
                                    {results.tracks.data.map(track => (
                                        <div key={track.id || track.providerIds[0]?.id} className="flex gap-3 items-center px-3 py-2 hover:bg-gray-700/50 rounded-lg cursor-pointer transition-colors">
                                            {track.albums && track.albums.length > 0 && track.albums[0].coverArtUrl ? (
                                                <img src={track.albums[0].coverArtUrl} alt={track.albums[0].title} className="h-12 w-12 rounded shadow-sm object-cover" />
                                            ) : (
                                                <div className="h-12 w-12 rounded bg-gray-600 flex items-center justify-center">
                                                    <Music className="h-6 w-6 text-gray-400" />
                                                </div>
                                            )}
                                            <div className="flex-1 min-w-0">
                                                <p className="text-sm text-white truncate">{track.name}</p>
                                                <p className="text-xs text-gray-400 truncate">
                                                    {track.artists.map(a => a.name).join(", ")}
                                                    {track.albums && track.albums.length > 0 && ` • ${track.albums[0].title}`}
                                                </p>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}

                            {/* Artists */}
                            {results.artists.data.length > 0 && (
                                <div className="p-2 border-t border-gray-700/50">
                                    <h3 className="px-3 py-2 flex gap-2 text-xs text-gray-400 uppercase tracking-wider items-center">
                                        <User className="h-4 w-4" /> {t('search.tabs.artists')}
                                    </h3>
                                    {results.artists.data.map(artist => (
                                        <div key={artist.id || artist.providerIds[0]?.id} className="flex gap-3 items-center px-3 py-2 hover:bg-gray-700/50 rounded-lg cursor-pointer transition-colors">
                                            {artist.imageUrl ? (
                                                <img src={artist.imageUrl} alt={artist.name} className="h-12 w-12 rounded-full object-cover" />
                                            ) : (
                                                <div className="h-12 w-12 rounded-full bg-gray-700 mr-3 flex items-center justify-center">
                                                    <User className="h-6 w-6 text-gray-500" />
                                                </div>
                                            )}
                                            <div className="flex-1 min-w-0">
                                                <p className="text-sm text-white">{artist.name}</p>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}

                            {/* Albums */}
                            {results.albums.data.length > 0 && (
                                <div className="p-2 border-t border-gray-700/50">
                                    <h3 className="px-3 py-2 flex gap-2 text-xs text-gray-400 uppercase tracking-wider items-center">
                                        <Disc className="h-4 w-4" /> {t('search.tabs.albums')}
                                    </h3>
                                    {results.albums.data.map(album => (
                                        <div key={album.id || album.providerIds[0]?.id} className="flex gap-3 items-center px-3 py-2 hover:bg-gray-700/50 rounded-lg cursor-pointer transition-colors">
                                            {album.coverArtUrl ? (
                                                <img src={album.coverArtUrl} alt={album.title} className="h-12 w-12 rounded shadow-sm object-cover" />
                                            ) : (
                                                <div className="h-12 w-12 rounded bg-gray-600 mr-3 flex items-center justify-center">
                                                    <Disc className="h-6 w-6 text-gray-400" />
                                                </div>
                                            )}
                                            <div className="flex-1 min-w-0">
                                                <p className="text-sm text-white truncate">{album.title}</p>
                                                {album.artists && album.artists.length > 0 && (
                                                    <p className="text-xs text-gray-400">{album.artists[0].name}</p>
                                                )}
                                                {album.releaseDate && (
                                                    <p className="text-xs text-gray-400">{` • ${new Date(album.releaseDate).toLocaleDateString(i18n.language)}`}</p>
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}

                            <div className="p-2 border-t border-gray-700/50">
                                <button
                                    onClick={handleSearchSubmit}
                                    className="w-full py-2 px-4 bg-gray-700 hover:bg-gray-600 text-sm font-medium text-white rounded-lg transition-colors"
                                >
                                    {t('components.layout.search_bar.see_all', { query: searchInput })}
                                </button>
                            </div>
                        </div>
                    ) : (
                        <div className="p-6 text-center text-gray-400">
                            {t('components.layout.search_bar.no_results_for', { query: searchInput })}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default SearchBar;