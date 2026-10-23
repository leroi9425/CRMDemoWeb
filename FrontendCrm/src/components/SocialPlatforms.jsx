export // Cấu hình các nền tảng mạng xã hội
const SOCIAL_PLATFORMS = [
    {
        id: "facebook",
        name: "Facebook Page",
        subTitle: "Quản lý các Fanpage Facebook đã kết nối.",
        idLabel: "Page ID",
        emptyText: "Chưa có Facebook Page nào được kết nối",
        connectBtnText: "Kết nối Facebook Page",
        connectBtnColor: "bg-[#1877F2] hover:bg-[#166FE5]",
        icon: (
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="currentColor">
                <path d="M24 12.073C24 5.405 18.627 0 12 0S0 5.405 0 12.073C0 18.099 4.388 23.093 10.125 24v-8.437H7.078v-3.49h3.047V9.413c0-3.025 1.792-4.704 4.533-4.704 1.312 0 2.686.235 2.686.235v2.973h-1.514c-1.491 0-1.955.93-1.955 1.886v2.27h3.328l-.532 3.49h-2.796V24C19.612 23.093 24 18.099 24 12.073z" />
            </svg>
        ),
    },
    {
        id: "zalo",
        name: "Zalo OA",
        subTitle: "Quản lý các tài khoản Zalo Official Account đã kết nối.",
        idLabel: "OA ID",
        emptyText: "Chưa có tài khoản Zalo OA nào được kết nối",
        connectBtnText: "Kết nối Zalo OA",
        connectBtnColor: "bg-[#0068FF] hover:bg-[#0058DB]",
        icon: (
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12.49 10.2722v-.4496h1.3467v6.3218h-.7704a.576.576 0 01-.5763-.5729l-.0006.0005a3.273 3.273 0 01-1.9372.6321c-1.8138 0-3.2844-1.4697-3.2844-3.2823 0-1.8125 1.4706-3.2822 3.2844-3.2822a3.273 3.273 0 011.9372.6321l.0006.0005zM6.9188 7.7896v.205c0 .3823-.051.6944-.2995 1.0605l-.03.0343c-.0542.0615-.1815.206-.2421.2843L2.024 14.8h4.8948v.7682a.5764.5764 0 01-.5767.5761H0v-.3622c0-.4436.1102-.6414.2495-.8476L4.8582 9.23H.1922V7.7896h6.7266zm8.5513 8.3548a.4805.4805 0 01-.4803-.4798v-7.875h1.4416v8.3548H15.47zM20.6934 9.6C22.52 9.6 24 11.0807 24 12.9044c0 1.8252-1.4801 3.306-3.3066 3.306-1.8264 0-3.3066-1.4808-3.3066-3.306 0-1.8237 1.4802-3.3044 3.3066-3.3044zm-10.1412 5.253c1.0675 0 1.9324-.8645 1.9324-1.9312 0-1.065-.865-1.9295-1.9324-1.9295s-1.9324.8644-1.9324 1.9295c0 1.0667.865 1.9312 1.9324 1.9312zm10.1412-.0033c1.0737 0 1.945-.8707 1.945-1.9453 0-1.073-.8713-1.9436-1.945-1.9436-1.0753 0-1.945.8706-1.945 1.9453 0 1.0746.8697 1.9453 1.945 1.9453z" />
            </svg>
        ),
    },
    {
        id: "tiktok",
        name: "TikTok",
        subTitle: "Quản lý các kênh TikTok Shop & Creator đã kết nối.",
        idLabel: "Account / Shop ID",
        emptyText: "Chưa có tài khoản TikTok nào được kết nối",
        connectBtnText: "Kết nối TikTok",
        connectBtnColor: "bg-[#010101] hover:bg-[#222222]",
        icon: (
            <i className="fa-brands fa-tiktok text-sm"></i>
        ),
    },
    {
        id: "shopee",
        name: "Shopee",
        subTitle: "Quản lý các gian hàng Shopee đã kết nối.",
        idLabel: "Shop ID",
        emptyText: "Chưa có gian hàng Shopee nào được kết nối",
        connectBtnText: "Kết nối Shopee",
        connectBtnColor: "bg-[#EE4D2D] hover:bg-[#D73211]",
        icon: (
            <i className="fa-solid fa-bag-shopping text-sm"></i>
        ),
    },
];