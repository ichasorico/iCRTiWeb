	<%
	String isAdmin;
	if(null == session.getAttribute("isAdmin")){
		isAdmin = "false";
	}else{
		isAdmin = "true";
	}
	String sello = null;
	if(null != session.getAttribute("sello")){
		sello = session.getAttribute("sello").toString();
	}
	
	Long timeStamp = Long.parseLong("0");
	if (null != session.getAttribute("timeStamp")){
		timeStamp = Long.parseLong(session.getAttribute("timeStamp").toString());	
	}
	
	Long timeStamped = Long.parseLong("1");
	if (null != session.getAttribute("timeStamped")){
		timeStamped = Long.parseLong(session.getAttribute("timeStamped").toString()); 
	}
	
	Long tmSession = timeStamped - timeStamp;  
	
	%>
	
	
    
    
<head>

  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="generator" content="Mobirise v2.6.1, mobirise.com">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="shortcut icon" href="../assets/images/discover-mobile-350x350-16.png" type="image/x-icon">
  <meta name="description" content="Free Bootstrap Blog Template">
  <title>Acceso admin iCRTiWeb</title>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:700,400&amp;subset=cyrillic,latin,greek,vietnamese">
  <link rel="stylesheet" href="../assets/bootstrap/css/bootstrap.min.css">
  <link rel="stylesheet" href="../assets/mobirise/css/style.css">
  <link rel="stylesheet" href="../assets/mobirise-slider/style.css">
  <link rel="stylesheet" href="../assets/mobirise-gallery/style.css">
  <link rel="stylesheet" href="../assets/mobirise/css/mbr-additional.css" type="text/css">
  
  
</head>
<body>

<section class="mbr-navbar mbr-navbar--freeze mbr-navbar--absolute mbr-navbar--transparent mbr-navbar--sticky mbr-navbar--auto-collapse" id="menu-59">
    <div class="mbr-navbar__section mbr-section">
        <div class="mbr-section__container container">
            <div class="mbr-navbar__container">
                <div class="mbr-navbar__column mbr-navbar__column--s mbr-navbar__brand">
                    <span class="mbr-navbar__brand-link mbr-brand mbr-brand--inline">
                        <span class="mbr-brand__logo"><a href="https://mobirise.com/bootstrap-template/"><img class="mbr-navbar__brand-img mbr-brand__img" src="../assets/images/discover-mobile-350x350-53.png" alt="Mobirise"></a></span>
                        <span class="mbr-brand__name"><a class="mbr-brand__name text-white" href="javascript:document.getElementById('getIndexForm').submit();">iCRTi Consulting S.L.</a></span>
                    </span>
                </div>
                <div class="mbr-navbar__hamburger mbr-hamburger text-white"><span class="mbr-hamburger__line"></span></div>
                <div class="mbr-navbar__column mbr-navbar__menu">
                    <nav class="mbr-navbar__menu-box mbr-navbar__menu-box--inline-right">
                        <div class="mbr-navbar__column"><ul class="mbr-navbar__items mbr-navbar__items--right mbr-buttons mbr-buttons--freeze mbr-buttons--right btn-decorator mbr-buttons--active"><li class="mbr-navbar__item"><a class="mbr-buttons__link btn text-white" href="one-page.html">ONE PAGE</a></li> <li class="mbr-navbar__item"><a class="mbr-buttons__link btn text-white" href="slider.html">SLIDER</a></li><li class="mbr-navbar__item"><a class="mbr-buttons__link btn text-white" href="video-background.html">VIDEO BG</a></li> <li class="mbr-navbar__item"><a class="mbr-buttons__link btn text-white" href="blog.html">BLOG</a></li></ul></div>
                        <div class="mbr-navbar__column"><ul class="mbr-navbar__items mbr-navbar__items--right mbr-buttons mbr-buttons--freeze mbr-buttons--right btn-inverse mbr-buttons--active"><li class="mbr-navbar__item"><a class="mbr-buttons__btn btn btn-default" href="https://mobirise.com/bootstrap-template/mobirise-free-template.zip">DOWNLOAD</a></li></ul></div>
                    </nav>
                </div>
            </div>
        </div>
    </div>
</section>

<section class="content-2 simple col-1 col-undefined mbr-parallax-background mbr-after-navbar" id="content5-77" style="background-image: url(../assets/images/iphone-6-458151-1920-1920x1285-67.jpg);">
    <div class="mbr-overlay" style="opacity: 0.6; background-color: rgb(0, 0, 0);"></div>
    <div class="container">
        <div class="row">
            <div>
                <div class="thumbnail">
                    <div class="caption">
                        <h3>Administración del Sistema</h3>                        
                        <div><p>El usuario es admin = <%=isAdmin %><br>Conexi�n <span id="tiempo"><%=tmSession %></span> <%=sello%></p></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

	
	
    <script>

    var synccounter = 0; //if you plant to sync with server
    var timeleft = 120 * 60; //initialize the regressive counter


    // check the state every 30 seconds
    setInterval(function(){checkcounter();}, 10 *1000);

    function checkcounter(){
       var params="operacion=pingLogin&sello=<%=sello%>";       
       $.ajax({url:"<%=request.getContextPath()%>/ServMain",
           type:"POST",
           data:params,
           success: function(result){
              if(!result.startsWith("LoginOK!!")){            	  
            		            	
        	   		window.location.href=result;
        	   	
           		}else{
           			
           			var res = result.split("!!");           			
           			document.getElementById("tiempo").innerHTML=res[1];
           		}
              
           }
       });
    }
       
    
    </script>


<section class="mbr-section" id="header3-78">
    <div class="mbr-section__container container mbr-section__container--first">
        <div class="mbr-header mbr-header--wysiwyg row">
            <div class="col-sm-8 col-sm-offset-2">
                <h3 class="mbr-header__text">DROP-DEAD EASY BOOTSTRAP BLOG&nbsp;</h3>
                <p class="mbr-header__subtext">By John Smith posted July 30, 2016</p>
            </div>
        </div>
    </div>
</section>

<section class="mbr-section" id="content1-79">
    <div class="mbr-section__container container mbr-section__container--middle">
        <div class="row">
            <div class="mbr-article mbr-article--wysiwyg col-sm-8 col-sm-offset-2"><p>
            	<form id="logedForm" action="<%=request.getContextPath()%>/ServMain" method="post">
					<input type="hidden" name="operacion" id="operacion" value="logOut" />
					<input type="hidden" name="sello" id="sello" value="<%=sello%> %>" />
					<input type="submit" value="LogOUT"/>
				</form>

           </div>
        </div>
    </div>
</section>





<footer class="mbr-section mbr-section--relative mbr-section--fixed-size" id="footer1-76" style="background-color: rgb(68, 68, 68);">
    
    <div class="mbr-section__container container">
        <div class="mbr-footer mbr-footer--wysiwyg row">
            <div class="col-sm-12">
                <p class="mbr-footer__copyright"></p><p>iCRTi Consulting S.L. <a href="https://mobirise.com/bootstrap-template/license.txt" class="text-gray">License</a></p><p></p>
            </div>
        </div>
    </div>
</footer>

<%@ include file="../../WEB-INF/incl/navServlet.jsp" %>

  <script src="../assets/jquery/jquery.min.js"></script>
  <script src="../assets/bootstrap/js/bootstrap.min.js"></script>
  <script src="../assets/smooth-scroll/SmoothScroll.js"></script>
  <script src="../assets/jarallax/jarallax.js"></script>
  <script src="../assets/bootstrap-carousel-swipe/bootstrap-carousel-swipe.js"></script>
  <script src="../assets/masonry/masonry.pkgd.min.js"></script>
  <script src="../assets/imagesloaded/imagesloaded.pkgd.min.js"></script>
  <script src="../assets/mobirise/js/script.js"></script>
  <script src="../assets/mobirise-gallery/script.js"></script>
  
  
</body>    