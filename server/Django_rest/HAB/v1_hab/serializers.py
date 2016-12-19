from rest_framework import serializers
from v1_hab.models import HouseholdAccountBook, STATE_CHOICES, CATEGORY_CHOICES, Photo
from django.utils import timezone
import os
from django.conf import settings
from sorl.thumbnail import get_thumbnail
# from sorl.thumbnail import delete

class HouseholdAccountBookSerializer(serializers.Serializer):
    pk = serializers.IntegerField(read_only=True)
    date = serializers.DateField(default=timezone.now)
    price = serializers.IntegerField()
    state = serializers.ChoiceField(choices=STATE_CHOICES)
    category = serializers.ChoiceField(choices=CATEGORY_CHOICES, default='default')
    priority = serializers.IntegerField(default=0)
    memo = serializers.CharField(allow_blank=True, max_length=100)

    def create(self, validated_data):
        return HouseholdAccountBook.objects.create(**validated_data)

    def update(self, instance, validated_data):
        instance.date = validated_data.get('date', instance.date)
        instance.price = validated_data.get('price', instance.price)
        instance.state = validated_data.get('state', instance.state)
        instance.category = validated_data.get('category', instance.category)
        instance.priority = validated_data.get('priority', instance.priority)
        instance.memo = validated_data.get('memo', instance.memo)
        instance.save()
        return instance


class PhotoSerializer(serializers.Serializer):
    pk = serializers.IntegerField(read_only=True)
    image_file = serializers.ImageField()
    description = serializers.CharField(max_length=500)
    created_at = serializers.DateTimeField(default=timezone.now)

    image_thumb_file = serializers.SerializerMethodField('get_thumb', read_only=True)

    def get_thumb(self, obj):
        print('-----')
        print(obj.image_file)
        # return 1
        return get_thumbnail(obj.image_file, '100x100', crop='center', quality=99).url

    def create(self, validated_data):
        return Photo.objects.create(**validated_data)

    def update(self, instance, validated_data):
        if instance.image_file != validated_data.get('image_file', instance.image_file):
            print('1')
            # 기존 image_file과 현재 image_file이 다르면 파일경로에서 기존 image_file 삭제
            # os.remove(os.path.join(settings.MEDIA_ROOT, instance.image_file.path))
            # cached db에 있는 thumbnail도 제거
            # delete(instance.image_file)

        instance.image_file = validated_data.get('image_file', instance.image_file)
        instance.description = validated_data.get('description', instance.description)
        instance.created_at = validated_data.get('created_at', instance.created_at)

        instance.save()
        return instance
