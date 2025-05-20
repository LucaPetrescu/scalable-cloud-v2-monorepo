import React from "react";

import { FiCommand, FiSearch } from "react-icons/fi";


export const Search = () => {
    return (
        <div>
            <div className="bg-stone-200 mb-4 relative rounded flex items-center px-2 py-1.5 text-sm">
            <input type="text" placeholder="Search" />
        </div>
        </div>
        
    )
}