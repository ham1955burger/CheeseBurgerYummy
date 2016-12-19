from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.views import APIView
from rest_framework.response import Response
from v1_hab.models import HouseholdAccountBook, Photo
from v1_hab.serializers import HouseholdAccountBookSerializer, PhotoSerializer
from django_cleanup.signals import cleanup_pre_delete, cleanup_post_delete
from django.conf import settings



# signals 적용
def sorl_delete(**kwargs):
    from sorl.thumbnail import delete
    delete(kwargs['file'])

# empty directories 지우는 방법 찾아보기
# def deleted_empty_directories(**kwargs):
#     import os
#     for root, dirs, files in os.walk(os.path.join(settings.MEDIA_ROOT)):
#         print("root=" + root)
#         # print("dirs=" + dirs)
#         for d in dirs:
#             print("directory = " + d)
#             dir = os.path.join(root, d)
#             # check if dir is empty
#             if not os.listdir(dir):
#                 os.rmdir(dir)

cleanup_pre_delete.connect(sorl_delete)
# cleanup_post_delete.connect(deleted_empty_directories)

@api_view(['GET', 'POST'])
def list(request):
    if request.method == 'GET':
        householdAccountBook = HouseholdAccountBook.objects.all()
        serializer = HouseholdAccountBookSerializer(householdAccountBook, many=True)
        return Response(serializer.data)

    elif request.method == 'POST':
        serializer = HouseholdAccountBookSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET', 'PUT', 'DELETE'])
def detail(request, pk):
    try:
        householdAccountBook = HouseholdAccountBook.objects.get(pk=pk)
    except HouseholdAccountBook.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == 'GET':
        serializer = HouseholdAccountBookSerializer(householdAccountBook)
        return Response(serializer.data)

    elif request.method == 'PUT':
        print(request.data)
        serializer = HouseholdAccountBookSerializer(householdAccountBook, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    elif request.method == 'DELETE':
        householdAccountBook.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class PhotoList(APIView):
    def get(self, request, format=None):
        photo = Photo.objects.all()
        serializer = PhotoSerializer(photo, many=True)
        return Response(serializer.data)

    def post(self, request, format=None):
        serializer = PhotoSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class PhotoDetail(APIView):
    def get_object(self, pk):
        try:
            return Photo.objects.get(pk=pk)
        except Photo.DoesNotexist:
            raise Http404

    def get(self, request, pk, format=None):
        photo = self.get_object(pk)
        serializer = PhotoSerializer(photo)
        return Response(serializer.data)
    
    def post(self, request, pk, format=None):
        photo = self.get_object(pk)
        serializer = PhotoSerializer(photo, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def put(self, request, pk, format=None):
        print(request.data)
        photo = self.get_object(pk)
        serializer = PhotoSerializer(photo, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        photo = self.get_object(pk)
        photo.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
