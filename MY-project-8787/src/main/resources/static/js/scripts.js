
window.addEventListener('DOMContentLoaded', event => {
	
	// 導覽列 收起來
	var navbarShrink = function() {
		const navbarCollapsible = document.body.querySelector('#mainNav');
		if (!navbarCollapsible) {
			return;
		}
		if (window.scrollY === 0) {
			navbarCollapsible.classList.remove('navbar-shrink')
		} else {
			navbarCollapsible.classList.add('navbar-shrink')
		}

	};
	navbarShrink();
	document.addEventListener('scroll', navbarShrink); // 頁面捲動時縮小導覽列


	// Activate Bootstrap scrollspy on the main nav element ( 在主導航元素上啟動 Bootstrap 滾動監視 )
	const mainNav = document.body.querySelector('#mainNav');
	if (mainNav) {
		new bootstrap.ScrollSpy(document.body, {
			target: '#mainNav',
			rootMargin: '0px 0px -40%',
		});
	};


	// Collapse responsive navbar when toggler is visible ( 當切換器可見時折疊響應式導覽列 )
	const navbarToggler = document.body.querySelector('.navbar-toggler');
	const responsiveNavItems = [].slice.call(
		document.querySelectorAll('#navbarResponsive .nav-link')
	);
	responsiveNavItems.map(function(responsiveNavItem) {
		responsiveNavItem.addEventListener('click', () => {
			if (window.getComputedStyle(navbarToggler).display !== 'none') {
				navbarToggler.click();
			}
		});
	});


	// 顯示大照片 Get the modal element ( 取得模態元素 )
	var portfolioModal = document.getElementById('portfolioModal');
	// Add event listener for when the modal is shown ( 新增模式顯示時的事件偵聽器 )
	portfolioModal.addEventListener('show.bs.modal', function(event) {
		var button = event.relatedTarget; // Button that triggered the modal
		var title = button.getAttribute('data-title'); // Get title from data-* attribute
		var text = button.getAttribute('data-text'); // Get text from data-* attribute
		var img = button.getAttribute('data-img'); // Get image URL from data-* attribute
		var modalTitle = portfolioModal.querySelector('.modal-title'); // Modal title element
		var modalImg = portfolioModal.querySelector('#modalImg'); // Modal image element
		var modalText = portfolioModal.querySelector('#modalText'); // Modal text element
		var modalTitleText = portfolioModal.querySelector('#modalTitle'); // Modal title text element
		modalTitle.textContent = title; // Set modal title text
		modalImg.src = img; // Set modal image source
		modalText.textContent = text; // Set modal description text
		modalTitleText.textContent = title; // Set modal title text
	});
	
	
	// 註冊表單
//	document.getElementById("registrationForm").addEventListener("submit", function(event) {
//		var password = document.getElementById("register-password").value;
//		var confirmPassword = document.getElementById("register-checkPassword").value;
//		var phoneRegex = /^09\d{2}\d{3}\d{3}$/;
//		var phone = document.getElementById("register-phone").value;

//		if (!phoneRegex.test(phone)) {
//			event.preventDefault();
//			document.getElementById("register-phone").setCustomValidity("手機號碼錯誤!");
//		} else {
//			document.getElementById("register-phone").setCustomValidity("");
//		}

//		if (password === "") {
//			event.preventDefault();
//			document.getElementById("register-password").setCustomValidity("請輸入密碼");
//		} else if (!passwordRegex.test(password)) {
//			event.preventDefault();
//			document.getElementById("register-password").setCustomValidity("密碼長度至少為8個字符，且包含至少一個數字、一個小寫字母和一個大寫字母");
//		} else {
//			document.getElementById("register-password").setCustomValidity("");
//		}

//		if (confirmPassword === "") {
//			event.preventDefault();
//			document.getElementById("register-checkPassword").setCustomValidity("請確認密碼");
//		} else if (password !== confirmPassword) {
//			event.preventDefault();
//			document.getElementById("register-checkPassword").setCustomValidity("兩次輸入的密碼不一致");
//		} else {
//			alert("註冊完成，歡迎登陸");
//		}
//	});

// renew registration form validation
//document.getElementById("registrationForm").addEventListener("submit", function(event) {
//    var password = document.getElementById("register-password").value;
//    var confirmPassword = document.getElementById("register-checkPassword").value;
//    var phoneRegex = /^09\d{8}$/; // 更新正則表達式以匹配正確的台灣手機號碼格式
//    var phone = document.getElementById("register-phone").value;
//    var passwordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/; // 至少一個數字、一個小寫字母和一個大寫字母，且長度至少為8個字符

//    var formValid = true;

//    if (!phoneRegex.test(phone)) {
//        formValid = false;
//        document.getElementById("register-phone").setCustomValidity("手機號碼錯誤!");
//    } else {
//        document.getElementById("register-phone").setCustomValidity("");
//    }

//    if (password === "") {
//        formValid = false;
//        document.getElementById("register-password").setCustomValidity("請輸入密碼");
//    } else if (!passwordRegex.test(password)) {
//        formValid = false;
//        document.getElementById("register-password").setCustomValidity("密碼長度至少為8個字符，且包含至少一個數字、一個小寫字母和一個大寫字母");
//    } else {
//        document.getElementById("register-password").setCustomValidity("");
//    }

//    if (confirmPassword === "") {
//        formValid = false;
//        document.getElementById("register-checkPassword").setCustomValidity("請確認密碼");
//    } else if (password !== confirmPassword) {
//        formValid = false;
//        document.getElementById("register-checkPassword").setCustomValidity("兩次輸入的密碼不一致");
//    } else {
//        document.getElementById("register-checkPassword").setCustomValidity("");
//    }

//    if (!formValid) {
//        event.preventDefault();
//    } else {
//       alert("註冊完成，歡迎登陸");
//    }
//});
	

 });
 

