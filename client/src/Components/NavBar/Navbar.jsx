import { React, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { FaHome, FaBriefcase, FaClipboardList, FaCog } from "react-icons/fa";
import logo from './../Assets/CareerCrack-logo530.png'
import open from './../Assets/ui/open.png';
import minimze from './../Assets/ui/minimize.png';
import close from './../Assets/ui/close.png';
import openHover from './../Assets/ui/open-hover.png';
import minimzeHover from './../Assets/ui/minimize-hover.png';
import closeHover from './../Assets/ui/close-hover.png';
import './Navbar.css';

export const Navbar = () => {
  const [navbarStyle, setNavBarStyle] = useState('minimize');
  const [isHovered, setIsHovered] = useState(false);
  
  const location = useLocation();
  let selectedTab = location.pathname;
  /*let navbarStyle = 'minimize';
  let navbarMode = 'nohover';*/
  let navbarImage = navbarStyle + '-' + (isHovered ? 'hover' : 'nohover');
  const navModetoPng = new Map([
    ['open-nohover', open],
    ['minimize-nohover', minimze],
    ['close-nohover', close],
    ['open-hover', openHover],
    ['minimize-hover', minimzeHover],
    ['close-hover', closeHover]
  ])

  const handleMouseEnter = () => {
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
  }

  const handleNavbarToggle = () => {
    if(navbarStyle === 'minimize') {
      setNavBarStyle('open');
    }
    else if(navbarStyle === 'open') {
      setNavBarStyle('minimize');
    }
  }

  return (
    <nav className='navbar'>
      <div className='navHeader'>
        <div className='nav-control'>
          <button 
          onMouseEnter={handleMouseEnter} 
          onMouseLeave={handleMouseLeave}
          onClick={handleNavbarToggle}
          >
            <img src={navModetoPng.get(navbarImage)} />
          </button>
        </div>
        <div className='logo-name'>
          <img src={logo} alt='CareerCrack Logo' height={"48px"}/>
          CareerCrack
        </div>
      </div>
      <div className='links'>
        <Link to={"/home"} className={selectedTab==="/home" ? "selected": ''}>
          <FaHome />
          <span>Home</span>
        </Link>
        <Link to={"/problems"} className={selectedTab==="/problems" ? "selected": ''}>
          <FaClipboardList />
          <span>Problems</span>
        </Link>
        <Link to={"/applications"} className={selectedTab==="/applications" ? "selected": ''}>
          <FaBriefcase />
          <span>Applications</span>
        </Link>
        <Link to={"/settings"} className={selectedTab==="/settings" ? "selected": ''}>
          <FaCog />
          <span>Settings</span>
        </Link>
      </div>
    </nav>
  )
}