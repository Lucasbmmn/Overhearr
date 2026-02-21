import { Search, XCircleIcon } from "lucide-react";
import { useState } from "react";
import { useTranslation } from "react-i18next";

const SearchBar = () => {
    const { t } = useTranslation();
    const [searchInput, setSearchInput] = useState("");

    return (
        <div className="relative flex flex-1 items-center text-white focus-within:text-gray-200 bg-gray-900/80 backdrop-blur-sm">
            <div className="pointer-events-none absolute inset-y-0 left-4 flex items-center">
                <Search className="h-5 w-5" />
            </div>
            <input
                className="block w-full rounded-full border border-gray-600 py-2 pl-10 text-white placeholder-gray-300 hover:border-gray-500 focus:border-gray-500 focus:bg-opacity-100 focus:placeholder-gray-400 focus:outline-none focus:ring-0"
                placeholder={t("components.layout.search_bar.search_place_holder")}
                type="search"
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                onKeyUp={(e) => {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        (e.target as HTMLInputElement).blur();
                    }
                }}
            />
            {searchInput.length > 0 && (
                <button
                    className="absolute inset-y-0 right-2 m-auto h-7 w-7 border-none p-1 text-gray-400 outline-none transition hover:text-white focus:border-none focus:outline-none"
                    onClick={() => {setSearchInput("")}}
                >
                    <XCircleIcon className="h-5 w-5" />
                </button>
            )}
        </div>
    )
}

export default SearchBar;