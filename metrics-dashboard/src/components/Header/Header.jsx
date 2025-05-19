import React from "react";
import Logo from "../Logo/Logo";
import "./header.css";
import Searchbar from "../Searchbar/Searchbar";

function Header() {
  return (
    <header id="header" className="header fixed-top d-flex align-items-center">
      <Logo />
      <Searchbar />
    </header>
  );
}

export default Header;
