"""HouseholdAccountBook URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.10/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.conf.urls import url
from rest_framework.urlpatterns import format_suffix_patterns
from v1_hab import views
from django.conf.urls.static import static
from django.conf import settings
# from django.contrib import admin

urlpatterns = [
    # url(r'^admin/', admin.site.urls),
    # for Web
    url(r'^scheme$', views.scheme, name='scheme'),

    # for REST
    url(r'^list/$', views.list),
    url(r'^detail/(?P<pk>[0-9]+)$', views.detail),
    url(r'^photo$', views.PhotoList.as_view()),
    url(r'^photo/detail/(?P<pk>[0-9]+)$', views.PhotoDetail.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)

if settings.DEBUG:
    urlpatterns += static(
        settings.MEDIA_URL, document_root=settings.MEDIA_ROOT
    )
