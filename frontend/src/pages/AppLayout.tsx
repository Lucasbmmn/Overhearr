import { Menu } from "lucide-react";
import React from "react";
import { Outlet } from "react-router-dom";
import SearchBar from "../components/layout/SearchBar";
import Sidebar from "../components/layout/Sidebar";
import UserDropDown from "../components/layout/UserDropDown";

const AppLayout = () => {
    const [sidebarOpen, setSidebarOpen] = React.useState(false);

    return (
        <div className="text-white bg-gray-900">
            <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

            <div className="relative flex-1 lg:ml-64 h-full">
                <header className="fixed flex top-0 left-0 lg:left-64 right-0 h-16 z-10 px-4 gap-3 items-center justify-between bg-gray-900/50 backdrop-blur-xs">
                    <button
                        onClick={() => setSidebarOpen(true)}
                        className="lg:hidden text-gray-400 hover:text-white cursor-pointer"
                    >
                        <Menu className="h-6 w-6" />
                    </button>
                    <SearchBar />
                    <UserDropDown />
                </header>

                <main className="relative top-16 pb-6 px-4">
                    <Outlet />
                </main>
            </div>
        </div>
    )
}

export default AppLayout;