import { useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useSearchParams } from "react-router-dom";
import { Music, User, Disc, Search, Loader2 } from "lucide-react";
import { searchService } from "../services/searchService";
import { SearchType, type SearchResponse } from "../types/search";
import { getResultKey, deduplicateResponse } from "../utils/searchUtils";

const SearchPage = () => {
    const { t, i18n } = useTranslation();
    const [searchParams, setSearchParams] = useSearchParams();
    const query = searchParams.get("q") || "";
    const tabParam = searchParams.get("tab") || "all";
    
    const [results, setResults] = useState<SearchResponse | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [isFetchingMore, setIsFetchingMore] = useState(false);
    const [hasMore, setHasMore] = useState(false);
    const [pageOffset, setPageOffset] = useState(0);

    const activeTab = (tabParam as SearchType || tabParam === "all")
        ? (tabParam as "all" | SearchType)
        : "all";

    const handleTabChange = (newTab: string) => {
        setSearchParams(prev => {
            const next = new URLSearchParams(prev);
            if (newTab === "all") {
                next.delete("tab");
            } else {
                next.set("tab", newTab);
            }
            return next;
        }, { replace: true });
    };

    const fetchMore = useCallback(async () => {
        if (activeTab !== "all" && !isFetchingMore && hasMore && results) {
            setIsFetchingMore(true);
            try {
                const nextPage = pageOffset + 1;
                const data = await searchService.search(query, [activeTab], nextPage, 25);
                
                setResults(prevResults => {
                    if (!prevResults) return data;

                    const newResults = { ...prevResults };
                    
                    const updateSection = <K extends keyof SearchResponse>(k: K) => {
                        const existingIds = new Set(prevResults[k].data.map(getResultKey));
                        const uniqueNewData = data[k].data.filter(item => !existingIds.has(getResultKey(item)));
                        newResults[k] = {
                            ...data[k],
                            data: [...prevResults[k].data, ...uniqueNewData] as any
                        };
                    };

                    if (activeTab === SearchType.TRACK) updateSection("tracks");
                    else if (activeTab === SearchType.ARTIST) updateSection("artists");
                    else if (activeTab === SearchType.ALBUM) updateSection("albums");

                    return newResults;
                });

                setPageOffset(nextPage);
                const key = activeTab === SearchType.TRACK ? "tracks" : activeTab === SearchType.ARTIST ? "artists" : "albums";
                setHasMore(data[key].meta.page < data[key].meta.totalPages - 1);
            } catch (error) {
                console.error("Failed to fetch more results:", error);
            } finally {
                setIsFetchingMore(false);
            }
        }
    }, [activeTab, isFetchingMore, hasMore, results, query, pageOffset]);

    const observer = useRef<IntersectionObserver | null>(null);
    const lastElementRef = useCallback((node: HTMLDivElement) => {
        if (!isLoading && !isFetchingMore) {
            if (observer.current) observer.current.disconnect();
            observer.current = new IntersectionObserver(entries => {
                if (entries[0].isIntersecting && hasMore && activeTab !== "all") {
                    fetchMore();
                }
            });
        }
        if (node && observer.current) observer.current.observe(node);
    }, [isLoading, isFetchingMore, hasMore, activeTab, fetchMore]);

    useEffect(() => {
        if (query.trim().length >= 2) {
            const fetchResults = async () => {
                setIsLoading(true);
                setPageOffset(0);
                try {
                    const types = activeTab === "all" ? [] : [activeTab];
                    const data = await searchService.search(query, types, 0, 25);
                    const deduplicatedData = deduplicateResponse(data);
                    setResults(deduplicatedData);
                    
                    if (activeTab !== "all") {
                        const key = activeTab === SearchType.TRACK ? "tracks" : activeTab === SearchType.ARTIST ? "artists" : "albums";
                        setHasMore(data[key].meta.page < data[key].meta.totalPages - 1);
                    } else {
                        setHasMore(false);
                    }
                } catch (error) {
                    console.error("Search failed:", error);
                } finally {
                    setIsLoading(false);
                }
            };

            fetchResults();
        }
    }, [query, activeTab]);

    const renderEmptyState = () => (
        <div className="flex flex-col items-center justify-center py-20 text-gray-400 animate-fade-in">
            <Search className="h-16 w-16 mb-4 opacity-20" />
            <h2 className="text-xl font-medium text-gray-300">{t('search.no_results')}</h2>
            <p className="mt-2">{t('search.search_else')}</p>
        </div>
    );

    const renderLoading = () => (
        <div className="flex flex-col items-center justify-center py-20 text-indigo-400">
            <Loader2 className="h-12 w-12 animate-spin mb-4" />
            <p className="text-gray-400">{t('search.searching_for', { query })}</p>
        </div>
    );

    return (
        <div className="animate-fade-in max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            {/* Header */}
            <div className="mb-8 p-6 rounded-2xl bg-gray-800/40 backdrop-blur-md border border-gray-700/50 shadow-xl">
                <div className="flex items-center gap-4 mb-6">
                    <div className="p-3 bg-indigo-500/20 rounded-xl border border-indigo-500/30">
                        <Search className="h-6 w-6 text-indigo-400" />
                    </div>
                    <div>
                        <h1 className="text-3xl font-bold text-white tracking-tight">
                            {t('search.results_for')} <span className="text-indigo-400">"{query}"</span>
                        </h1>
                        <p className="text-gray-400 mt-1">{t('search.discover_prompt')}</p>
                    </div>
                </div>

                {/* Tabs */}
                <div className="flex gap-2 p-1 bg-gray-900/50 rounded-xl w-fit border border-gray-700/30">
                    {[
                        { id: "all", label: t('search.tabs.all'), icon: Search },
                        { id: SearchType.TRACK, label: t('search.tabs.tracks'), icon: Music },
                        { id: SearchType.ARTIST, label: t('search.tabs.artists'), icon: User },
                        { id: SearchType.ALBUM, label: t('search.tabs.albums'), icon: Disc },
                    ].map((tab) => (
                        <button
                            key={tab.id}
                            onClick={() => handleTabChange(tab.id as any)}
                            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 ${
                                activeTab === tab.id
                                    ? "bg-indigo-600 text-white shadow-lg shadow-indigo-900/40"
                                    : "text-gray-400 hover:text-gray-200 hover:bg-gray-800/80"
                            }`}
                        >
                            <tab.icon className="h-4 w-4" />
                            {tab.label}
                        </button>
                    ))}
                </div>
            </div>

            {isLoading ? renderLoading() : (
                results ? (
                    <div className="space-y-12">
                        {/* Tracks Section */}
                        {(activeTab === "all" || activeTab === SearchType.TRACK) && results.tracks.data.length > 0 && (
                            <section>
                                <div className="flex items-center justify-between mb-4 px-2">
                                    <h2 className="text-xl font-semibold text-white flex items-center gap-2">
                                        <Music className="h-5 w-5 text-indigo-400" /> {t('search.tabs.tracks')}
                                    </h2>
                                    <span className="text-xs text-gray-500 uppercase tracking-widest">{t('search.meta.results_count', { count: results.tracks.meta.totalElements })}</span>
                                </div>
                                <div className="grid gap-2">
                                    {results.tracks.data.map((track) => (
                                        <div key={getResultKey(track)} className="group flex gap-3 items-center p-3 rounded-xl bg-gray-800/20 border border-transparent hover:border-gray-700/50 hover:bg-gray-700/30 transition-all cursor-pointer">
                                            {track.albums && track.albums.length > 0 && track.albums[0].coverArtUrl ? (
                                                <img src={track.albums[0].coverArtUrl} alt={track.albums[0].title} className="h-16 w-16 rounded-lg shadow-sm object-cover" />
                                            ) : (
                                                <div className="h-16 w-16 rounded-lg bg-gray-800 flex items-center justify-center mr-4 group-hover:bg-indigo-500/10 transition-colors">
                                                    <Music className="h-8 w-8 text-gray-500 group-hover:text-indigo-400" />
                                                </div>
                                            )}
                                            <div className="flex-1 min-w-0">
                                                <h4 className="text-sm font-semibold text-white group-hover:text-indigo-400 truncate">{track.name}</h4>
                                                <p className="text-xs text-gray-400 truncate mt-0.5">
                                                    {track.artists.map(a => a.name).join(", ")}
                                                    {track.albums && track.albums.length > 0 && ` • ${track.albums[0].title}`}
                                                </p>
                                            </div>
                                            <div className="mr-5 text-xs text-gray-400 transition-colors">
                                                {track.durationMs ? `${Math.floor(track.durationMs / 60000)}:${((track.durationMs % 60000) / 1000).toFixed(0).padStart(2, '0')}` : ""}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </section>
                        )}

                        {/* Artists Section */}
                        {(activeTab === "all" || activeTab === SearchType.ARTIST) && results.artists.data.length > 0 && (
                            <section>
                                <div className="flex items-center justify-between mb-4 px-2">
                                    <h2 className="text-xl font-semibold text-white flex items-center gap-2">
                                        <User className="h-5 w-5 text-indigo-400" /> {t('search.tabs.artists')}
                                    </h2>
                                    <span className="text-xs text-gray-500 uppercase tracking-widest">{t('search.meta.results_count', { count: results.artists.meta.totalElements })}</span>
                                </div>
                                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-1">
                                    {results.artists.data.map((artist) => (
                                        <div key={getResultKey(artist)} className="group text-center p-5 border border-transparent hover:border-gray-700/50 hover:bg-gray-700/30 rounded-md hover:cursor-pointer">
                                            <div className="relative aspect-square mb-3 overflow-hidden rounded-full bg-gray-800 border-2 border-transparent group-hover:border-indigo-500/50 transition-all duration-300">
                                                {artist.imageUrl ? (
                                                    <img src={artist.imageUrl} alt={artist.name} className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
                                                ) : (
                                                    <div className="w-full h-full flex items-center justify-center">
                                                        <User className="h-12 w-12 text-gray-600" />
                                                    </div>
                                                )}
                                            </div>
                                            <h4 className="text-sm font-semibold text-white group-hover:text-indigo-400 transition-colors truncate">{artist.name}</h4>
                                        </div>
                                    ))}
                                </div>
                            </section>
                        )}

                        {/* Albums Section */}
                        {(activeTab === "all" || activeTab === SearchType.ALBUM) && results.albums.data.length > 0 && (
                            <section>
                                <div className="flex items-center justify-between mb-4 px-2">
                                    <h2 className="text-xl font-semibold text-white flex items-center gap-2">
                                        <Disc className="h-5 w-5 text-indigo-400" /> {t('search.tabs.albums')}
                                    </h2>
                                    <span className="text-xs text-gray-500 uppercase tracking-widest">{t('search.meta.results_count', { count: results.albums.meta.totalElements })}</span>
                                </div>
                                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-1">
                                    {results.albums.data.map((album) => (
                                        <div key={getResultKey(album)} className="group p-5 border border-transparent hover:border-gray-700/50 hover:bg-gray-700/30 rounded-md hover:cursor-pointer">
                                            <div className="relative aspect-square mb-3 overflow-hidden rounded-xl bg-gray-800 shadow-lg border-2 border-gray-700/30 group-hover:border-indigo-500/50 transition-all duration-300">
                                                {album.coverArtUrl ? (
                                                    <img src={album.coverArtUrl} alt={album.title} className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
                                                ) : (
                                                    <div className="w-full h-full flex items-center justify-center">
                                                        <Disc className="h-12 w-12 text-gray-600" />
                                                    </div>
                                                )}
                                            </div>
                                            <h4 className="text-sm font-semibold text-white group-hover:text-indigo-400 truncate mb-0.5">{album.title}</h4>
                                            <p className="text-xs text-gray-400 flex justify-between items-center">
                                                {album.artists && album.artists.length > 0 && (
                                                    <span className="text-xs text-gray-400">{album.artists[0].name}</span>
                                                )}
                                                {album.releaseDate && (
                                                    <span className="text-xs text-gray-400">{` • ${new Date(album.releaseDate).toLocaleDateString(i18n.language)}`}</span>
                                                )}
                                            </p>
                                        </div>
                                    ))}
                                </div>
                            </section>
                        )}

                        {/* Infinite Scroll Sentinel */}
                        {activeTab !== "all" && hasMore && (
                            <div ref={lastElementRef} className="flex justify-center py-8">
                                <Loader2 className="h-8 w-8 text-indigo-400 animate-spin" />
                            </div>
                        )}

                        {/* No more results message */}
                        {activeTab !== "all" && !hasMore && results[activeTab === SearchType.TRACK ? "tracks" : activeTab === SearchType.ARTIST ? "artists" : "albums"].data.length > 0 && (
                            <div className="text-center py-8 text-gray-500 text-sm italic">
                                {t('search.no_more_results')}
                            </div>
                        )}

                        {/* No results in specific tab or all empty */}
                        {((activeTab !== "all" && results[activeTab === SearchType.TRACK ? "tracks" : activeTab === SearchType.ARTIST ? "artists" : "albums"].data.length === 0) || 
                          (activeTab === "all" && results.tracks.data.length === 0 && results.artists.data.length === 0 && results.albums.data.length === 0)) && 
                          renderEmptyState()}
                    </div>
                ) : renderEmptyState()
            )}
        </div>
    );
};

export default SearchPage;
